import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mobile_flutter/services/encryption_service.dart';

class AuthService {
    static const _storage = FlutterSecureStorage();
    final String baseUrl = "http://localhost:8080/api/auth";

    Future<bool> register(String email, String fullName, String password) async {
        final response = await http.post(
            Uri.parse('$baseUrl/register'),
            headers: {'Content-Type':'application/json'},
            body: jsonEncode({
                'email': email,
                'fullName': fullName,
                'password': password
            }),
        );

        if (response.statusCode == 200) {
            await EncryptionService.init(password);

            final data = jsonDecode(response.body);
            await _storage.write(key: 'jwt_token', value: data['token']);

            return true;
        } else {
            print("Register failed: ${response.body}");
            return false;
        }
    }

    Future<bool> login(String email, String password) async {
        final response = await http.post(
            Uri.parse('$baseUrl/login'),
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({
                'email': email,
                'password': password
            }),  
        );
        if (response.statusCode == 200) {
            final data = jsonDecode(response.body);

            await EncryptionService.init(password);

            await _storage.write(key: 'jwt_token', value: data['token']);

            print("Login successfull for ${data['fullName']}");
            return true;
        } else {
            print("Login failed");
            return false;
        }
    }
}