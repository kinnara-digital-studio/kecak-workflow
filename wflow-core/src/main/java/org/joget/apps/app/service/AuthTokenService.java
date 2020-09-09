package org.joget.apps.app.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClock;
import org.joget.commons.util.SetupManager;
import org.joget.directory.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service("authTokenService")
public class AuthTokenService implements Serializable {
    transient
    private static final long serialVersionUID = -3301605591108950415L;
    private Clock clock = DefaultClock.INSTANCE;


    private final static String DEFAULT_SECRET = "It's not secure to use default key";
    private static final String ISSUER = "org.kecak";

    @Nonnull private final String secret;

    private final Long expiration = 600L;
    private final Long expirationRefreshToken = 300L;

    public AuthTokenService() {
        String masterPassword = SetupManager.getSettingValue(SetupManager.MASTER_LOGIN_PASSWORD);
        secret = masterPassword.isEmpty() ? DEFAULT_SECRET : masterPassword;
    }

    public String getUsernameFromToken(String token) throws ExpiredJwtException {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getIssuedAtDateFromToken(String token) throws ExpiredJwtException {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    public Date getExpirationDateFromToken(String token) throws ExpiredJwtException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Get All Claims as map
     * @param token
     * @return map
     * @throws ExpiredJwtException
     */
    public Claims getClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(getSecret())
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) throws ExpiredJwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, getSecret())
                .compact();
    }

    public String generateRefreshToken(String tokenOldId, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpDateRefToken(createdDate);
        Map<String, Object> claims = new HashMap<>();
        claims.put("old_id", tokenOldId);
        return doGenerateToken(claims, subject);
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) throws ExpiredJwtException {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    public String refreshToken(String token, String refToken) throws Exception {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        try {
            getClaims(token);
            throw new Exception("Claims not expired");
        } catch(ExpiredJwtException e) {
            final Claims claims = e.getClaims();
            Claims refClaims = getClaims(refToken);
            if(claims.getId().equals(refClaims.get("old_id"))) {
                claims.setIssuedAt(createdDate);
                claims.setExpiration(expirationDate);
                return doGenerateToken(claims, claims.getSubject());
            } else {
                throw new Exception("Invalid token/refresh token");
            }
        }
    }

    public Boolean validateToken(String token, User user) throws ExpiredJwtException {
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        //final Date expiration = getExpirationDateFromToken(token);
        return (user != null && username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private @Nonnull Date calculateExpDateRefToken(Date createdDate) {
        return new Date(createdDate.getTime() + expirationRefreshToken * 1000);
    }

    private @Nonnull Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    public @Nonnull String getSecret() {
        return secret;
    }
}