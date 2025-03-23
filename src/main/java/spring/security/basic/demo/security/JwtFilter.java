package spring.security.basic.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.security.basic.demo.service.CustomUserDetailsService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Retrieve the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;     // Exit the method since there is no valid token
        }

        // Extract the JWT token from the Authorization header (removing "Bearer " prefix)
        String token = authorizationHeader.substring(7);
        try {
    String userName = jwtUtil.extractUserNameFromToken(token);

            // If a username is found and no authentication exists earlier in the SecurityContext, validate the token
    if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        // Load user details from the database using the username
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);
        if(jwtUtil.validateToken(token, userDetails)) {
            // Create an authentication object containing user details
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(), userDetails.getAuthorities());
            // Set additional request details for authentication
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Set the authentication object in the SecurityContext & tells to spring boot application as authentication is done
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }
        } catch (Exception e) {
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("error","Invalid token");

            // Convert the response map to a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(responseMap);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(jsonString);             // Write the JSON error message to the response body
            return;             // Exit method to prevent further processing
        }
        // Continue the filter chain after processing the JWT
        filterChain.doFilter(request, response);
    }



}
