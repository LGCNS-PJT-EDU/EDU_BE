package com.education.takeit.oauth.dto;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class OIDCPublicKey implements Serializable {
	private String kid;
	private String alg;
	private String kty;
	private String use;
	private String n;
	private String e;
}
