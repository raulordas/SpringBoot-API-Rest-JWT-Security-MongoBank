package com.raul.mongobank.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.ResponseStatusException;

import com.google.gson.JsonObject;
import com.raul.mongobank.entities.Usuario;
import com.raul.mongobank.services.ServiceUsuarios;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private static Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationFilter.class);
	private AuthenticationManager authManager;
	private Date expiration_date;
	
	@Autowired
	private ServiceUsuarios serviceUsuarios;
	
	public JWTAuthenticationFilter(AuthenticationManager authManager) {
		this.authManager = authManager;
		setFilterProcessesUrl("/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String header = request.getHeader("Authorization");
		header = header.substring(6, header.length());
		byte[] res = Base64.getDecoder().decode(header);
		String credentials = new String(res, StandardCharsets.UTF_8);
		String[] finalCredentials = credentials.split(":", 2);
		LOGGER.info(credentials);
		return authManager.authenticate(new UsernamePasswordAuthenticationToken(finalCredentials[0], finalCredentials[1]));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		UserDetails user = (UserDetails) authResult.getPrincipal();
		LOGGER.info(authResult.getAuthorities().toString());
		List<String> roles = authResult.getAuthorities()
				.stream().map(authority -> authority.getAuthority())
				.collect(Collectors.toList());
		LOGGER.info("AUTENTICADO {}", roles.toString());
		expiration_date = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION);
		
		
		List<Usuario> usuarios = serviceUsuarios.findUserByUsername(user.getUsername());
		
		if (usuarios == null || usuarios.size() == 0) {
			throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED);
		}
		
		String token = Jwts.builder()
	            .signWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes()), SignatureAlgorithm.HS512)
	            .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
	            .setIssuer(SecurityConstants.TOKEN_ISSUER)
	            .setAudience(SecurityConstants.TOKEN_AUDIENCE)
	            .setSubject(user.getUsername())
	            .setExpiration(expiration_date)
	            .claim("rol", roles)
	            .compact();
		
		response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter body = response.getWriter();
		JsonObject objeto = new JsonObject();
		objeto.addProperty("username", user.getUsername());
		objeto.addProperty("user_id", usuarios.get(0).getId());
		objeto.addProperty("rol", roles.toArray().toString());
		objeto.addProperty(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
		objeto.addProperty("expiration", expiration_date.toInstant().atZone(ZoneId.of("Europe/Paris")).toString());
		body.print(objeto);
		body.flush();
		body.close();
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		PrintWriter p = response.getWriter();
		p.write("FRACASO");
		p.flush();
	}

	
}
