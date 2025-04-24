package com.education.takeit.oauth.service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.education.takeit.oauth.dto.OIDCPublicKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OidcPublicKeyService {

	private final OidcJwkCacheService oidcJwkCacheService;

	public RSAPublicKey getMatchingKey(String kid, String alg) {
		OIDCPublicKey matchedKey = oidcJwkCacheService.getCachedPublicKeys().getMatchedKey(kid, alg);
		return toPublicKey(matchedKey);
	}

	private RSAPublicKey toPublicKey(OIDCPublicKey key) {
		try {
			BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.getN()));
			BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.getE()));
			return (RSAPublicKey)KeyFactory.getInstance("RSA")
				.generatePublic(new RSAPublicKeySpec(modulus, exponent));
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate RSA public key", e);
		}
	}
}
