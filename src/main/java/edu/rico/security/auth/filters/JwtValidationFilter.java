package edu.rico.security.auth.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static edu.rico.security.auth.TokenJwtConfig.*;

public class JwtValidationFilter extends BasicAuthenticationFilter{

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse reponse, FilterChain chain) throws IOException, ServletException{
        String header = request.getHeader(HEADER_AUTHORIZATION);
        if(header == null || !header.startsWith(PREFIX_TOKEN)){
            chain.doFilter(request, reponse);
            return;
        }
        String token = header.replace(PREFIX_TOKEN, "");
        byte[] tokenDecodeBytes = Base64.getDecoder().decode(token);
        String tokenDecodeString = new String(tokenDecodeBytes);
        String[] tokenParts = tokenDecodeString.split("\\.");
        String secret = tokenParts[0];
        String username = tokenParts[1];
        logger.info(String.format("El usuario que viene dentro del token es %s", username));
        logger.info(String.format("El secreto que viene dentro del token es %s", secret));


        if(SECRET_KEY.equals(secret)){
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, reponse);
        }else{
            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Token incorrecto");
            reponse.getWriter().write(new ObjectMapper().writeValueAsString(body));
            reponse.setStatus(403);
            reponse.setContentType("application/json");
        }


    }

}
