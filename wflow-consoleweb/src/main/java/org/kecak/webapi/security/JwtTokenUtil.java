package org.kecak.webapi.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;


import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil implements Serializable {


    transient
    private static final long serialVersionUID = -3301605591108950415L;
    private Clock clock = DefaultClock.INSTANCE;


    private final String secret = "s3cR3t";

    private final Long expiration = new Long(600);
    private final Long expirationRefreshToken = new Long(300);

    public String getUsernameFromToken(String token) throws ExpiredJwtException, Exception{
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) throws ExpiredJwtException, Exception {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) throws ExpiredJwtException, Exception {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException, Exception{
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException, Exception{
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) throws ExpiredJwtException, Exception {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateRefreshToken(String tokenOldId, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpDateRefToken(createdDate);
        Map<String, Object> claims = new HashMap<>();
        claims.put("old_id", tokenOldId);
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) throws ExpiredJwtException, Exception {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token, String refToken) throws Exception {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        try {
            Claims claims = getAllClaimsFromToken(token);
            throw new Exception("Claims not expired");
        } catch(ExpiredJwtException e) {
            final Claims claims = e.getClaims();
            Claims refClaims = getAllClaimsFromToken(refToken);
            if(claims.getId().equals(refClaims.get("old_id"))) {
                claims.setIssuedAt(createdDate);
                claims.setExpiration(expirationDate);

                return Jwts.builder()
                        .setClaims(claims)
                        .signWith(SignatureAlgorithm.HS512, secret)
                        .compact();
            }
            else {
                throw new Exception("Invalid token/refresh token");
            }
        }
    }

    public Boolean validateToken(String token, User user) throws ExpiredJwtException, Exception{
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        //final Date expiration = getExpirationDateFromToken(token);
        return (
                user != null
                        && username.equals(user.getUsername())
                        && !isTokenExpired(token)
        );
    }

    private Date calculateExpDateRefToken(Date createdDate) {
        return new Date(createdDate.getTime() + expirationRefreshToken * 1000);
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

}