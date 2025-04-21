package com.education.takeit.oauth.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class PublicKeyUtil {
	public static RSAPublicKey createRSAPublicKey(String n, String e) {
		BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(n));
		BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(e));
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return (RSAPublicKey)keyFactory.generatePublic(new RSAPublicKeySpec(modulus, exponent));
		} catch (Exception ex) {
			throw new RuntimeException("공개키 생성 실패", ex);
		}
	}
}
