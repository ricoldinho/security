package edu.rico.security.auth.filters;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.rico.security.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static edu.rico.security.auth.TokenJwtConfig.*;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        User user = null;
        String username = null;
        String password = null;

        try {
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();
            logger.info("Username que llega en el InputStream: " + username);
            logger.info("Password que llega en el InputStream: " + password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authToken);
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
            String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername();
            Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
            boolean isAdmin = roles.stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
            Claims claims = Jwts.claims();
            claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
            claims.put("isAdmin", isAdmin);
            String token = Jwts.builder()
                            .setClaims(claims)
                            .setSubject(username)
                            .setIssuedAt(new Date())
                            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                            .signWith(SECRET_KEY).compact();

            response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("mensaje", String.format("Hola %s, has iniciado sesión con éxito", username));
            body.put("username", username);
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(200);
            response.setContentType("application/json");
            
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error en el login!!!!! username o password incorrecto");
        body.put("error", failed.getMessage());
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }
}
