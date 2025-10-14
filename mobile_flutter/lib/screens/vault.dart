import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:mobile_flutter/services/encryption_service.dart';
import 'login.dart';

class VaultScreen extends StatefulWidget {
  const VaultScreen({super.key});

  @override
  State<VaultScreen> createState() => _VaultScreenState();
}

class _VaultScreenState extends State<VaultScreen> {
  final _storage = const FlutterSecureStorage();
  EncryptionService? _encryption;
  final String baseUrl = "http://localhost:8080/api";
  List<Map<String, dynamic>> _passwords = [];

  @override
  void initState() {
    super.initState();
    _loadPasswords();
  }

  Future<void> _loadPasswords() async {
    final token = await _storage.read(key: 'jwt_token');
    if (token == null) return;

    final response = await http.get(
      Uri.parse('$baseUrl/passwords'),
      headers: {'Authorization': 'Bearer $token'},
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      if (_encryption == null) {
        final masterPassword = await _storage.read(key: 'master_password');

        if (masterPassword != null) {
            _encryption = await EncryptionService.init(masterPassword);
        }
      }

      setState(() {
        _passwords = data.map((p) {
          return {
            'service': p['serviceName'],
            'username': p['username'],
            'password': _encryption?.decryptText(p['encryptedPassword']) ?? '***',
          };
        }).toList();
      });
    }
  }

  Future<void> _logout() async {
    await _storage.deleteAll();
    if (mounted) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("🔐 My Vault"),
        actions: [
          IconButton(onPressed: _logout, icon: const Icon(Icons.logout)),
        ],
      ),
      body: _passwords.isEmpty
          ? const Center(child: Text("No passwords found."))
          : ListView.builder(
              itemCount: _passwords.length,
              itemBuilder: (context, index) {
                final item = _passwords[index];
                return ListTile(
                  title: Text(item['service'] ?? ''),
                  subtitle: Text("User: ${item['username']}"),
                  trailing: Text(item['password'] ?? ''),
                );
              },
            ),
    );
  }
}
