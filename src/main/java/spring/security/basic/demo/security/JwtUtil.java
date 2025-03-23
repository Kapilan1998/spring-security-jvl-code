package spring.security.basic.demo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

@Configuration
public class JwtUtil {

    @Value("${my.secret.key}")
    private String SECRET_KEY;


    public String generateToken(UserDetails userDetails){

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 *60 *2))
                .signWith(getSecretKey(),Jwts.SIG.HS256)
                .compact();             // once it is compacted it will concatenate above all things and generate jwt token
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
/**
 * Validates the provided JWT token by checking if the username extracted from it
 * matches the username from the given UserDetails.
 */
    public boolean validateToken(String token, UserDetails userDetails){
        return extractUserNameFromToken(token).equals(userDetails.getUsername());
    }

    public String extractUserNameFromToken(String token) {
        return Jwts.parser()           // Creates a JWT parser to decrypt the encrypted jwt token
                .verifyWith(getSecretKey())  // Verifies the token using the secret key
                .build()  // Builds the parser for validation
                .parseSignedClaims(token)  // Parses and verifies the signed JWT token
                .getPayload()  // Extracts the token's payload (claims)
                .getSubject();  // Retrieves the subject, which is typically the username
    }
}
