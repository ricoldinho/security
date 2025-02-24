package edu.rico.security.auth.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
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

import edu.rico.security.auth.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
        try{
            Claims claims = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
            Object authoritiesClaims = claims.get("authorities");
            Collection <? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
                                                                                    .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                                                                                    .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
            String username = claims.getSubject();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username,null,authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, reponse);
        }catch(JwtException e){
            Map<String, Object> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("mensaje", "Token inválido");
            reponse.getWriter().write(new ObjectMapper().writeValueAsString(body));
            reponse.setStatus(403);
            reponse.setContentType("application/json");
        }


    }

}
