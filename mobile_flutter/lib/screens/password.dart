import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:mobile_flutter/services/auth_service.dart';
import 'package:mobile_flutter/services/encryption_service.dart';

class AddPasswordScreen extends StatefulWidget {
  const AddPasswordScreen({super.key});

  @override
  State<AddPasswordScreen> createState() => _AddPasswordScreenState();
}

class _AddPasswordScreenState extends State<AddPasswordScreen> {
  final _formKey = GlobalKey<FormState>();
  final _serviceController = TextEditingController();
  final _urlController = TextEditingController();
  final _usernameController = TextEditingController();
  final _passwordController = TextEditingController();
  final _storage = const FlutterSecureStorage();

  bool _isLoading = false;
  EncryptionService? _encryption;
  final String baseUrl = "http://localhost:9090/api";

  Future<void> _savePassword() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      final auth = AuthService();
      final token = await _storage.read(key: 'jwt_token');
      final masterPassword = await _storage.read(key: 'master_password');

      if (token == null || masterPassword == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text("You must be logged in")),
        );
        return;
      }

      _encryption ??= await EncryptionService.init(masterPassword);

      final encryptedPassword =
          _encryption!.encryptText(_passwordController.text);

      final response = await http.post(
        Uri.parse('$baseUrl/passwords'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({
          'serviceName': _serviceController.text,
          'websiteUrl': _urlController.text,
          'username': _usernameController.text,
          'encryptedPassword': encryptedPassword,
        }),
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        if (mounted) Navigator.pop(context, true);
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text("Failed to save: ${response.body}")),
        );
      }
    } catch (e) {
      debugPrint("Error saving password: $e");
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("Error: $e")),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  void dispose() {
    _serviceController.dispose();
    _urlController.dispose();
    _usernameController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("➕ Add New Service")),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _serviceController,
                decoration: const InputDecoration(labelText: "Service Name"),
                validator: (v) =>
                    v == null || v.isEmpty ? "Enter a service name" : null,
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: _urlController,
                decoration:
                    const InputDecoration(labelText: "Website URL (optional)"),
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: _usernameController,
                decoration: const InputDecoration(labelText: "Username / Email"),
                validator: (v) =>
                    v == null || v.isEmpty ? "Enter your username" : null,
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: _passwordController,
                decoration: const InputDecoration(labelText: "Password"),
                obscureText: true,
                validator: (v) =>
                    v == null || v.isEmpty ? "Enter a password" : null,
              ),
              const SizedBox(height: 25),
              ElevatedButton.icon(
                onPressed: _isLoading ? null : _savePassword,
                icon: const Icon(Icons.save),
                label: Text(_isLoading ? "Saving..." : "Save"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
