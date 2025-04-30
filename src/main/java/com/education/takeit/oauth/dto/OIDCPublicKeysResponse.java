package com.education.takeit.oauth.dto;

import java.io.Serializable;
import java.util.List;

public record OIDCPublicKeysResponse(List<OIDCPublicKey> keys) implements Serializable {

  public OIDCPublicKey getMatchedKey(String kid, String alg) {
    return keys.stream()
        .filter(key -> kid.equals(key.kid()) && alg.equals(key.alg()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("공개키 매칭 실패"));
  }
}
