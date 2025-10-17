import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mobile_flutter/services/encryption_service.dart';

class VaultService {
  static const _storage = FlutterSecureStorage();
  final String baseUrl = "http://localhost:9090/api/passwords";
  late final EncryptionService _encryption;

  VaultService._(this._encryption);

  static Future<VaultService> init(String masterPassword) async {
    final encryption = await EncryptionService.init(masterPassword);
    return VaultService._(encryption);
  }

  Future<bool> addPassword(Required String serviceName,Required String username,Required String websiteUrl ,Required String password,Required String notes) async {
    final token = await _storage.read(key: 'jwt_token');
    if (token == null) {
      print("⚠️ No token found, please log in first.");
      return false;
    }

    final encryptedPassword = _encryption.encryptText(password);
    final encryptedNotes = _encryption.encryptText(notes);

    final body = jsonEncode({
      "serviceName": serviceName,
      "username": username,
      "websiteUrl": websiteUrl,
      "encryptedPassword": encryptedPassword,
      "notes": encryptedNotes,
    });

    final response = await http.post(
      Uri.parse(baseUrl),
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer $token"
      },
      body: body,
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      print("✅ Password saved successfully");
      return true;
    } else {
      print("❌ Error saving password: ${response.body}");
      return false;
    }
  }

  Future<List<Map<String, String>>> getPasswords() async {
    final token = await _storage.read(key: 'jwt_token');
    if (token == null) {
      print("⚠️ No token found.");
      return [];
    }

    final response = await http.get(
      Uri.parse(baseUrl),
      headers: {
        "Authorization": "Bearer $token"
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data.map((entry) {
        return {
          "serviceName": entry["serviceName"],
          "username": entry["username"],
          "websiteUrl": entry["websiteUrl"]
          "password": _encryption.decryptText(entry["encryptedPassword"]),
          "notes": _encryption.decryptText(entry["notes"] ?? ""),
        };
      }).toList();
    } else {
      print("❌ Error fetching passwords: ${response.body}");
      return [];
    }
  }

  Future<bool> deletePassword(String id) async {
    final token = await _storage.read(key: 'jwt_token');
    if (token == null) {
      print("⚠️ No token found.");
      return false;
    }

    final response = await http.delete(
      Uri.parse("$baseUrl/$id"),
      headers: {
        "Authorization": "Bearer $token"
      },
    );

    if (response.statusCode == 200 || response.statusCode == 204) {
      print("🗑️ Entry deleted successfully");
      return true;
    } else {
      print("❌ Error deleting entry: ${response.body}");
      return false;
    }
  }
}
