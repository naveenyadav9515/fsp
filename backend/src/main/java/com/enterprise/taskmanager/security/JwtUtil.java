package com.enterprise.taskmanager.security;

/*
 * ============================================================
 * JWT UTILITY — Creates and Validates JWT Tokens
 * ============================================================
 *
 * WHAT IS JWT?
 * JWT (JSON Web Token) is a way to securely transmit information
 * between two parties as a JSON object.
 *
 * HOW DOES IT WORK? (Step by Step)
 * ────────────────────────────────
 * 1. User sends username + password to /api/auth/login
 * 2. Server checks the credentials
 * 3. If correct, server CREATES a JWT token and sends it back
 * 4. User stores the token (in localStorage or a cookie)
 * 5. For every future request, user sends the token in the header:
 *      Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 * 6. Server VALIDATES the token before processing the request
 *
 * WHAT'S INSIDE A JWT TOKEN?
 * A JWT has 3 parts separated by dots: HEADER.PAYLOAD.SIGNATURE
 *   - Header:    Algorithm used (HS256)
 *   - Payload:   Data (username, role, expiry time)
 *   - Signature: Ensures the token hasn't been tampered with
 *
 * ============================================================
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// @Component tells Spring: "Create one instance of this class and manage it"
@Component
public class JwtUtil {

    // These values are read from application.properties
    // @Value("${property.name}") injects the value from the config file

    @Value("${jwt.secret}")
    private String secretKey;       // The secret key used to sign tokens

    @Value("${jwt.expiration}")
    private long expirationTime;    // How long the token is valid (in milliseconds)

    /*
     * STEP 1: Create the signing key from our secret string
     *
     * The secret key must be converted into a cryptographic key
     * that the JWT library can use for signing.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /*
     * STEP 2: GENERATE a JWT token
     *
     * This is called after a successful login.
     * It creates a token containing the username and role.
     *
     * @param username The user's username (stored in the token)
     * @param role     The user's role (stored in the token)
     * @return A JWT token string like "eyJhbGciOiJIUzI1NiJ9..."
     */
    public String generateToken(String username, String role) {
        Date now = new Date();                                        // Current time
        Date expiryDate = new Date(now.getTime() + expirationTime);   // When the token expires

        return Jwts.builder()
                .subject(username)                   // WHO this token belongs to
                .claim("role", role)                 // Custom data: the user's role
                .issuedAt(now)                       // WHEN the token was created
                .expiration(expiryDate)              // WHEN the token expires
                .signWith(getSigningKey())            // SIGN with our secret key
                .compact();                          // Build the final token string
    }

    /*
     * STEP 3: EXTRACT the username from a token
     *
     * This reads the "subject" field from the token's payload.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /*
     * STEP 4: EXTRACT the role from a token
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /*
     * STEP 5: VALIDATE a token
     *
     * A token is valid if:
     *   1. It was signed with our secret key (not tampered with)
     *   2. It hasn't expired
     *   3. The username matches the expected user
     */
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername.equals(username) && !isTokenExpired(token);
    }

    /*
     * HELPER: Check if the token has expired
     */
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /*
     * HELPER: Extract ALL claims (data) from the token
     *
     * "Claims" = the data stored inside the JWT payload
     * This method parses the token and verifies the signature.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())     // Verify the signature
                .build()
                .parseSignedClaims(token)        // Parse the token
                .getPayload();                   // Get the payload (claims)
    }
}
