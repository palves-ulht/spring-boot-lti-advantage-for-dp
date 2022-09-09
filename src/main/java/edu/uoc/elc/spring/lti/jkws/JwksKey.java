package edu.uoc.elc.spring.lti.jkws;

import lombok.Builder;
import lombok.Data;

// copied from spring-boot-lti-advantage-jkws to avoid having another dependency
@Data
@Builder
public class JwksKey {
    private final String kty;
    private final String alg;
    private final String use;
    private final String e;
    private final String n;
    private final String kid;
}
