import 'dart:convert';
import 'dart:typed_data';
import 'package:http/http.dart' as http;
import 'package:mobile_flutter/services/crypto_service.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_secure_storage_windows/flutter_secure_storage_windows.dart';
import 'package:mobile_flutter/services/encryption_service.dart';

class AuthService {
    static const _storage = FlutterSecureStorage(
        iOptions: const IOSOptions(),
        aOptions: const AndroidOptions(encryptedSharedPreferences: true),
        wOptions: const WindowsOptions(),
        lOptions: const LinuxOptions(),
    );
    final String baseUrl = "http://localhost:9090/api/auth";
    final _crypto = CryptoService();

    Future<bool> register(String email, String fullName, String password) async {
       try {

        final rsaPair = await _crypto.generateRsaKeyPair();
        final aesKey = _crypto.generateAesKey();
        final encryptedMaserKey = _crypto.rsaEncryptOaepBase64(aesKey, rsaPair.publicKey);
        final publicPem = _crypto.publicKeyToPem(rsaPair.publicKey);
        final privatePem = _crypto.privateKeyToPem(rsaPair.privateKey);

        final response = await http.post(
            Uri.parse('$baseUrl/register'),
            headers: {'Content-Type': 'application/json'},
            body: jsonEncode({
                'email': email,
                'fullName': fullName,
                'password': password,
                'publicKey': publicPem,
                'encryptedMasterKey': encryptedMaserKey,
            }),
        );

        if (response.statusCode == 200 || response.statusCode == 201) {
            final data = jsonDecode(response.body);
            await _storage.write(key: 'jwt_token', value: data['token']);
            await _storage.write(key: 'private_key', value: privatePem);
            await _storage.write(key: 'aes_key', value: base64Encode(aesKey));
            await _storage.write(key: 'master_password', value: password);
            await EncryptionService.init(password);
            print("Registration successful!");
            return true;
        } else {
            print("Register failed: ${response.body}");
            return false;
        }
       } catch (e) {
        print("Error during registration: $e");
        return false;
       }
    }

    Future<bool> login(String email, String password) async {
        try {
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
                await _storage.write(key: 'jwt_token', value: data['token']);
                await _storage.write(key: 'master_password', value: password);
                await EncryptionService.init(password);
                print("Login successfull for ${data['fullName']}");
                return true;
            } else {
                print("Login failed");
                return false;
            }
        } catch (e) {
            print("Error during login: $e");
            return false;
        }
    }

    Future<bool> logout() async {
       try {
         await _storage.deleteAll();
         print("Logged out successfuly!");
         return true;
       } catch (e) {
        print("Failed to logout: $e");
        return false;
       }
    }
       
    Future<Uint8List?> getLocalAesKey() async {
        final encoded = await _storage.read(key: 'aes_key');
        if (encoded == null) return null;
        return base64Decode(encoded);
    }
}