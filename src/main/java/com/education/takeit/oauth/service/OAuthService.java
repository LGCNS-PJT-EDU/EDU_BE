package com.education.takeit.oauth.service;

import com.education.takeit.oauth.dto.OAuthLoginRequest;
import com.education.takeit.oauth.dto.OAuthTokenResponse;
import java.util.Map;

public interface OAuthService {


	/**
	 * 소셜 로그인 메인 로직
	 * @param request
	 * @return
	 */
	String login(OAuthLoginRequest request);

  /**
   * 소셜 로그인 검증 로직 (서명 및 유효성 검사)
   *
   * @param token
   */
  Map<String, String> validateIdToken(OAuthTokenResponse token);
}
