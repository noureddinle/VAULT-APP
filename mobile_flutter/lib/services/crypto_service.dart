import 'dart:convert';
import 'dart:math';
import 'dart:typed_data';
import 'package:pointycastle/export.dart' as pc;
import 'package:asn1lib/asn1lib.dart';

Uint8List _randomBytes(int length) {
  final rnd = Random.secure();
  return Uint8List.fromList(List<int>.generate(length, (_) => rnd.nextInt(256)));
}

String _bytesToBase64(Uint8List b) => base64Encode(b);
Uint8List _base64ToBytes(String s) => base64Decode(s);

class RsaKeyPair {
  final pc.RSAPublicKey publicKey;
  final pc.RSAPrivateKey privateKey;
  RsaKeyPair(this.publicKey, this.privateKey);
}

class CryptoService {
  Future<RsaKeyPair> generateRsaKeyPair() async {
    final secureRandom = pc.FortunaRandom()
      ..seed(pc.KeyParameter(_randomBytes(32)));
    final params = pc.RSAKeyGeneratorParameters(BigInt.parse('65537'), 2048, 64);
    final keyGen = pc.RSAKeyGenerator()
      ..init(pc.ParametersWithRandom(params, secureRandom));
    final pair = keyGen.generateKeyPair();
    return RsaKeyPair(
      pair.publicKey as pc.RSAPublicKey,
      pair.privateKey as pc.RSAPrivateKey,
    );
  }

  String publicKeyToPem(pc.RSAPublicKey pub) {
    final seq = ASN1Sequence()
      ..add(ASN1Integer(pub.modulus!))
      ..add(ASN1Integer(pub.exponent!));
    final b64 = base64.encode(seq.encodedBytes);
    final chunks =
        RegExp('.{1,64}').allMatches(b64).map((m) => m.group(0)!).join('\n');
    return '-----BEGIN RSA PUBLIC KEY-----\n$chunks\n-----END RSA PUBLIC KEY-----';
  }

  String privateKeyToPem(pc.RSAPrivateKey priv) {
    final seq = ASN1Sequence()
      ..add(ASN1Integer(BigInt.zero))
      ..add(ASN1Integer(priv.modulus!))
      ..add(ASN1Integer(BigInt.parse('65537')))
      ..add(ASN1Integer(priv.exponent!))
      ..add(ASN1Integer(priv.p!))
      ..add(ASN1Integer(priv.q!))
      ..add(ASN1Integer(priv.exponent! % (priv.p! - BigInt.one)))
      ..add(ASN1Integer(priv.exponent! % (priv.q! - BigInt.one)))
      ..add(ASN1Integer(priv.q!.modInverse(priv.p!)));
    final b64 = base64.encode(seq.encodedBytes);
    final chunks =
        RegExp('.{1,64}').allMatches(b64).map((m) => m.group(0)!).join('\n');
    return '-----BEGIN RSA PRIVATE KEY-----\n$chunks\n-----END RSA PRIVATE KEY-----';
  }

  pc.RSAPublicKey parsePublicPem(String pem) {
    final b64 = pem.replaceAll(RegExp(r'-----.*?-----|\s'), '');
    final der = base64.decode(b64);
    final asn1Parser = ASN1Parser(der);
    final seq = asn1Parser.nextObject() as ASN1Sequence;
    final n = (seq.elements![0] as ASN1Integer).valueAsBigInteger!;
    final e = (seq.elements![1] as ASN1Integer).valueAsBigInteger!;
    return pc.RSAPublicKey(n, e);
  }

  pc.RSAPrivateKey parsePrivatePem(String pem) {
    final b64 = pem.replaceAll(RegExp(r'-----.*?-----|\s'), '');
    final der = base64.decode(b64);
    final asn1Parser = ASN1Parser(der);
    final seq = asn1Parser.nextObject() as ASN1Sequence;
    final n = (seq.elements![1] as ASN1Integer).valueAsBigInteger!;
    final d = (seq.elements![3] as ASN1Integer).valueAsBigInteger!;
    final p = (seq.elements![4] as ASN1Integer).valueAsBigInteger!;
    final q = (seq.elements![5] as ASN1Integer).valueAsBigInteger!;
    return pc.RSAPrivateKey(n, d, p, q);
  }

  String rsaEncryptOaepBase64(Uint8List data, pc.RSAPublicKey pub) {
    final engine = pc.OAEPEncoding(pc.RSAEngine());
    engine.init(true, pc.PublicKeyParameter<pc.RSAPublicKey>(pub));
    final out = _processInBlocks(engine, data);
    return _bytesToBase64(out);
  }

  Uint8List rsaDecryptOaepBase64(String base64Cipher, pc.RSAPrivateKey priv) {
    final engine = pc.OAEPEncoding(pc.RSAEngine());
    engine.init(false, pc.PrivateKeyParameter<pc.RSAPrivateKey>(priv));
    final out = _processInBlocks(engine, _base64ToBytes(base64Cipher));
    return out;
  }

  Uint8List _processInBlocks(pc.AsymmetricBlockCipher engine, Uint8List input) {
    final out = BytesBuilder();
    var offset = 0;
    final inputBlockSize = engine.inputBlockSize;
    while (offset < input.length) {
      final chunkSize = (offset + inputBlockSize <= input.length)
          ? inputBlockSize
          : input.length - offset;
      out.add(engine.process(input.sublist(offset, offset + chunkSize)));
      offset += chunkSize;
    }
    return out.toBytes();
  }

  Uint8List generateAesKey() => _randomBytes(32);
  Uint8List generateIv() => _randomBytes(12);

  String aesGcmEncryptBase64(Uint8List plaintext, Uint8List key) {
    final iv = generateIv();
    final cipher = pc.GCMBlockCipher(pc.AESEngine());
    final params = pc.AEADParameters(pc.KeyParameter(key), 128, iv, Uint8List(0));
    cipher.init(true, params);
    final ct = cipher.process(plaintext);
    final combined = Uint8List(iv.length + ct.length)
      ..setAll(0, iv)
      ..setAll(iv.length, ct);
    return _bytesToBase64(combined);
  }

  Uint8List aesGcmDecryptBase64(String base64Combined, Uint8List key) {
    final combined = _base64ToBytes(base64Combined);
    final iv = combined.sublist(0, 12);
    final ctTag = combined.sublist(12);
    final cipher = pc.GCMBlockCipher(pc.AESEngine());
    final params = pc.AEADParameters(pc.KeyParameter(key), 128, iv, Uint8List(0));
    cipher.init(false, params);
    return cipher.process(ctTag);
  }
}
