package com.treizer.spring_security_app.utils;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    public String createToken(Authentication authentication) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();

        // Get authorities
        String authorities = authentication.getAuthorities().stream()
                // Getting type String
                .map(GrantedAuthority::getAuthority)
                // Concat with "," each value
                .collect(Collectors.joining(","));

        // Creating Jason Web Token
        String jwtToken = JWT.create()
                // The User who generates the token
                .withIssuer(this.userGenerator)
                // The Subject for whom the token is generated
                .withSubject(username)
                // Generate the claims (values in the payload / body)
                .withClaim("authorities", authorities)
                // Issue date
                .withIssuedAt(new Date())
                // Expire date (30 mins.)
                .withExpiresAt(new Date(System.currentTimeMillis() + 1_800_000))
                // Unique ID
                .withJWTId(UUID.randomUUID().toString())
                // At what point will this token be considered valid? Now
                .withNotBefore(new Date(System.currentTimeMillis()))
                // Who signs? The encryption algorithm
                .sign(algorithm);

        return jwtToken;
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

            JWTVerifier verifier = JWT.require(algorithm)
                    // Who Issue the token
                    .withIssuer(this.userGenerator)
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;

        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException("Token invalido, no autorizado");
        }
    }

    public String extractUsername(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject().toString();
    }

    public Claim getClaimByName(DecodedJWT decodedJWT, String claimName) {
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims();
    }

}
