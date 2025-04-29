package com.education.takeit.oauth.service;

import com.education.takeit.oauth.dto.OIDCPublicKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OidcPublicKeyService {

    private final OidcJwkCacheService oidcJwkCacheService;

    /**
     * Kakao Oauth 키 매칭
     *
     * @param kid
     * @param alg
     * @return
     */
    public RSAPublicKey getMatchingKey(String kid, String alg) {
        OIDCPublicKey matchedKey = oidcJwkCacheService.getCachedPublicKeys().getMatchedKey(kid, alg);
        return toPublicKey(matchedKey);
    }

    /**
     * Kakao OAuth 공개 키
     *
     * @param key
     * @return
     */
    private RSAPublicKey toPublicKey(OIDCPublicKey key) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(key.n()));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(key.e()));
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate RSA public key", e);
        }
    }
}
