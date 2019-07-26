package com.raul.mongobank.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
		UsernamePasswordAuthenticationToken userAuth = getAuth(token, request, response);
		
		if (userAuth == null) {
			chain.doFilter(request, response);
			return;
		}
	
		SecurityContextHolder.getContext().setAuthentication(userAuth);
		chain.doFilter(request, response);
	}
	
	private UsernamePasswordAuthenticationToken getAuth(String token, HttpServletRequest request, HttpServletResponse response) {

		try {
			token = token.replace("Bearer ", "");
		
			String user = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody().getSubject();
			Object rol = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET.getBytes()).parseClaimsJws(token).getBody().get("rol");
			String splitter = rol.toString();
			splitter = splitter.replace("[", "");
			splitter = splitter.replace("]", "");
			String[] result = splitter.split(", ");
			ArrayList<GrantedAuthority> grants = new ArrayList<>();
			
			for (String aux: result) {
				grants.add(new SimpleGrantedAuthority(aux));
			}
			
			if (user != null) {
				return new UsernamePasswordAuthenticationToken(user, null, grants);
				
			}	
				
		} catch (ExpiredJwtException exception) {
			System.out.println("Request to parse expired JWT : {} failed : {}" + token + exception.getMessage());
			throw new ResponseStatusException(HttpStatus.CONFLICT, token + exception.getMessage());

		} catch (UnsupportedJwtException exception) {
			System.out
					.println("Request to parse unsupported JWT : {} failed : {}" + token + exception.getMessage());
		} catch (SignatureException exception) {
			
			System.out.println("Request to parse JWT with invalid signature : {} failed : {}" + token + exception.getMessage());

		} catch (MalformedJwtException exception) {
			System.out.println("Request to parse invalid JWT : {} failed : {}" + token + exception.getMessage());

		} catch (IllegalArgumentException exception) {
			System.out.println("Request to parse empty or null JWT : {} failed : {}" + token + exception.getMessage());

		}
		return null;
	}
}
