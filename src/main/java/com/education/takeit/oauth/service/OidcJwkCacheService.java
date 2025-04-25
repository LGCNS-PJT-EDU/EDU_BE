package com.education.takeit.oauth.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.education.takeit.oauth.client.KakaoOauthClient;
import com.education.takeit.oauth.dto.OIDCPublicKeysResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OidcJwkCacheService {

	private final KakaoOauthClient kakaoOauthClient;

	/**
	 * Kakao JWKS Redis 캐싱
	 * @return
	 */
	@Cacheable(value = "kakao-jwk", key = "'jwks'")
	public OIDCPublicKeysResponse getCachedPublicKeys() {
		return kakaoOauthClient.getPublicKeys();
	}
}

