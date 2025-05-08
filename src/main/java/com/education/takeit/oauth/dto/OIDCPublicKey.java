package com.education.takeit.oauth.dto;

import java.io.Serializable;

public record OIDCPublicKey(String kid, String alg, String kty, String use, String n, String e)
    implements Serializable {}
