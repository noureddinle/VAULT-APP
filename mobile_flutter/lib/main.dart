import 'package:flutter/material.dart';
import 'services/encryption_service.dart';
import 'screens/login.dart';
import 'screens/register.dart';
import 'screens/vault.dart';


void main() async {
 runApp(const MaterialApp(
  debugShowCheckedModeBanner: false,
  home: LoginScreen(),
 ));
}
