package com.education.takeit.oauth.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;

@Getter
public class OIDCPublicKeysResponse implements Serializable {
	private List<OIDCPublicKey> keys;

	public OIDCPublicKey getMatchedKey(String kid, String alg) {
		return keys.stream()
			.filter(key -> kid.equals(key.getKid()) && alg.equals(key.getAlg()))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("공개키 매칭 실패"));
	}
}
