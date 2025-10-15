import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:pointycastle/export.dart' as pc;

class SecureStorageService {
  static const _privateKeyKey = 'rsa_private_pem';

  // Mobile/desktop: Keychain/Keystore
  final FlutterSecureStorage _secure = const FlutterSecureStorage();

  // Web: store encrypted private key in localStorage; encryption is done with user password.
  // For web, we’ll expect caller to pass already-encrypted private key string.

  Future<void> savePrivateKeyPem(String pem,
      {String? webEncryptedPem}) async {
    if (kIsWeb) {
      // For web: caller must pass encrypted PEM (AES-GCM w/ PBKDF2 key)
      if (webEncryptedPem == null) {
        throw StateError('Web requires encrypted PEM');
      }
      // ignore: undefined_prefixed_name
      // use dart:html directly
      // (Put this snippet where you can import dart:html)
      // window.localStorage[_privateKeyKey] = webEncryptedPem;
      throw UnimplementedError('Call savePrivateKeyWebEncrypted in web layer');
    } else {
      await _secure.write(key: _privateKeyKey, value: pem);
    }
  }

  Future<String?> loadPrivateKeyPem() async {
    if (kIsWeb) {
      // Same comment as above (keep web storage in your web UI layer)
      throw UnimplementedError('Call loadPrivateKeyWebEncrypted in web layer');
    } else {
      return _secure.read(key: _privateKeyKey);
    }
  }
}
