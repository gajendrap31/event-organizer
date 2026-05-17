package com.eventorganizer.security.jwt;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eventorganizer.entity.User;
import com.eventorganizer.security.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
  
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${jwt.secret}")
	private String jwtSecret;
	
    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

	private String generateToken(UserDetailsImpl userDetailsImpl) {
		
		Map<String, Object> header = Map.of(
		    "alg", "HS256",
		    "typ", "JWT"
		);
	    
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + jwtExpirationMs);
	    return Jwts.builder().header().add(header)
    		.and()
    		.subject(String.valueOf(userDetailsImpl.getId()))
    		.claim("name", userDetailsImpl.getName())
    		.claim("email", userDetailsImpl.getEmail())
    		.claim("role", userDetailsImpl.getRole())
    		.issuedAt(issuedAt)
    		.expiration(expiration)
			.signWith(key(), SIG.HS256)
		.compact();
	}
	  
	private SecretKey key() {
		
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	public Claims getClaimsFromJwtToken(String token) {
	    
		if(token == null) return null;
	
		return Jwts.parser() 
	        .verifyWith(key())
	        .build()
	        .parseSignedClaims(token)
	        .getPayload();
	}
	
	public boolean validateJwtToken(String authToken) {
		
		try {
			Jwts.parser()
				.verifyWith(key())
				.build()
				.parse(authToken);
			
			return true;
		} catch(JwtException | IllegalArgumentException e) {
			logger.info("Error : {}", e.getMessage());
			return false;
		}
	}

	public String generateJwtToken(User user) {
		
		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(
					Math.toIntExact(user.getId()),
					user.getName(),
					user.getPassword(),
					user.getEmail(),
					user.getRole()
				);
		return generateToken(userDetailsImpl);
	}
	  
}
