import 'package:encrypt/encrypt.dart' as encrypt;
import 'dart:convert';
import 'package:crypto/crypto.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:typed_data';


class EncryptionService {
  static const _storage = FlutterSecureStorage();

  late final encrypt.Key key;
  late final encrypt.IV iv;
  late final encrypt.Encrypter encrypter;

  EncryptionService._(this.key, this.iv) {
    encrypter = encrypt.Encrypter(encrypt.AES(key, mode: encrypt.AESMode.cbc));
  }

  /// Initialize from a user's master password (or restore from secure storage)
  static Future<EncryptionService> init(String masterPassword) async {
    // Derive AES key (32 bytes for AES-256)
    final keyBytes = sha256.convert(utf8.encode(masterPassword)).bytes;
    final key = encrypt.Key(Uint8List.fromList(keyBytes));

    // Generate or reuse IV (16 bytes)
    String? savedIv = await _storage.read(key: "vault_iv");
    if (savedIv == null) {
      final randomIV = encrypt.IV.fromSecureRandom(16);
      await _storage.write(key: "vault_iv", value: base64Encode(randomIV.bytes));
      savedIv = base64Encode(randomIV.bytes);
    }

    final iv = encrypt.IV(base64Decode(savedIv));

    return EncryptionService._(key, iv);
  }

  /// Encrypt a plain string
  String encryptText(String plainText) {
    final encrypted = encrypter.encrypt(plainText, iv: iv);
    return encrypted.base64;
  }

  /// Decrypt an encrypted string
  String decryptText(String encryptedText) {
    return encrypter.decrypt64(encryptedText, iv: iv);
  }
}
