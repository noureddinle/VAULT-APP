import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'package:tesseract_ocr/tesseract_ocr.dart';
import 'package:flutter_zxing/flutter_zxing.dart';
import 'package:crypto/crypto.dart';
import 'package:mobile_flutter/services/encryption_service.dart';

class ParseAndUploadIdScreen extends StatefulWidget {
  const ParseAndUploadIdScreen({super.key});
  @override
  State<ParseAndUploadIdScreen> createState() => _ParseAndUploadIdScreenState();
}

class _ParseAndUploadIdScreenState extends State<ParseAndUploadIdScreen> {
  final picker = ImagePicker();
  final _storage = const FlutterSecureStorage();
  EncryptionService? _enc;

  Future<void> _process() async {
    final img = await picker.pickImage(source: ImageSource.camera);
    if (img == null) return;

    // ---- 1️⃣ Try open-source barcode decode (ZXing) ----
    Map<String, dynamic>? fields;
    final zxResult = await zx.scanImagePath(img.path);
    if (zxResult.text?.isNotEmpty ?? false) {
      fields = _parsePdf417Payload(zxResult.text!);
    }

    // ---- 2️⃣ Fallback to offline OCR (Tesseract) ----
    if (fields == null) {
      final text = await TesseractOcr.extractText(img.path, language: 'eng');
      final mrz = _extractMrzBlock(text);
      if (mrz != null) {
        fields = _parseMrz(mrz);
      }
    }

    if (fields == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not parse document')),
      );
      return;
    }

    // ---- 3️⃣ Normalize schema ----
    final docType = fields['docType'] ?? 'UNKNOWN';
    final plaintextJson = jsonEncode({
      "docType": docType,
      "givenName": fields["givenName"],
      "surname": fields["surname"],
      "docNumber": fields["docNumber"],
      "dob": fields["dob"],
      "doe": fields["doe"],
      "nationality": fields["nationality"],
      "issuingState": fields["issuingState"]
    });

    // ---- 4️⃣ Encrypt locally ----
    final masterPassword = await _storage.read(key: 'master_password');
    if (masterPassword == null) {
      ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Please login again.')));
      return;
    }

    _enc ??= await EncryptionService.init(masterPassword);
    final encryptedPayload = _enc!.encryptText(plaintextJson);
    final ivB64 = await _storage.read(key: 'vault_iv');

    // ---- 5️⃣ Compute SHA-256 hash for blockchain proof ----
    final sha = sha256.convert(utf8.encode(plaintextJson)).toString();

    // ---- 6️⃣ Upload encrypted data ----
    final token = await _storage.read(key: 'jwt_token');
    final resp = await http.post(
      Uri.parse('http://localhost:9090/api/documents'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode({
        "docType": docType,
        "encryptedPayload": encryptedPayload,
        "iv": ivB64,
        "schemaVersion": 1,
        "sha256": sha,
      }),
    );

    if (resp.statusCode == 201 || resp.statusCode == 200) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Document saved securely ✅')),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Upload failed: ${resp.body}')),
      );
    }
  }

  Map<String, dynamic>? _parsePdf417Payload(String raw) {
    // Example: you can split by delimiters for your country's format
    // Return null if unrecognized
    return {
      "docType": "DRIVER_LICENSE",
      "givenName": "JOHN",
      "surname": "DOE",
      "docNumber": "XYZ12345",
      "dob": "1998-07-21",
      "doe": "2030-07-21",
      "nationality": "MA",
      "issuingState": "MA"
    };
  }

  String? _extractMrzBlock(String text) {
    final lines = text.split('\n').map((l) => l.trim()).where((l) => l.length > 25).toList();
    final mrzLines = lines.where((l) => RegExp(r'^[A-Z0-9<]+$').hasMatch(l)).toList();
    if (mrzLines.length >= 2) {
      return mrzLines.sublist(mrzLines.length - 2).join('\n');
    }
    return null;
  }

  Map<String, String>? _parseMrz(String mrz) {
    final parts = mrz.split('\n');
    if (parts.length != 2) return null;
    final l1 = parts[0].padRight(44, '<');
    final l2 = parts[1].padRight(44, '<');
    final issuing = l1.substring(2, 5).replaceAll('<', '');
    final nameRaw = l1.substring(5).split('<<');
    final surname = nameRaw.isNotEmpty ? nameRaw[0].replaceAll('<', ' ').trim() : '';
    final given = nameRaw.length > 1 ? nameRaw[1].replaceAll('<', ' ').trim() : '';
    final docNumber = l2.substring(0, 9).replaceAll('<', '');
    final nationality = l2.substring(10, 13).replaceAll('<', '');
    String _yyMmDd(String s) {
      final yy = s.substring(0, 2), mm = s.substring(2, 4), dd = s.substring(4, 6);
      final year = int.parse(yy) >= 50 ? '19$yy' : '20$yy';
      return '$year-$mm-$dd';
    }
    final dob = _yyMmDd(l2.substring(13, 19));
    final doe = _yyMmDd(l2.substring(21, 27));

    return {
      "docType": "PASSPORT",
      "issuingState": issuing,
      "surname": surname,
      "givenName": given,
      "docNumber": docNumber,
      "nationality": nationality,
      "dob": dob,
      "doe": doe
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Scan & Encrypt ID')),
      body: Center(
        child: ElevatedButton.icon(
          icon: const Icon(Icons.document_scanner),
          onPressed: _process,
          label: const Text('Scan Document'),
        ),
      ),
    );
  }
}
