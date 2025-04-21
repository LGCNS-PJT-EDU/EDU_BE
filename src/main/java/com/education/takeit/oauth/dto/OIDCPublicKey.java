package com.education.takeit.oauth.dto;

import lombok.Getter;

@Getter
public class OIDCPublicKey {
	private String kid;
	private String alg;
	private String kty;
	private String use;
	private String n;
	private String e;
}
