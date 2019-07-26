package com.raul.mongobank.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.raul.mongobank.entities.Usuario;
import com.raul.mongobank.services.ServiceUsuarios;

@Component
public class CustomUserDetails implements UserDetailsService {
	
	@Autowired
	private ServiceUsuarios serviceUsuarios;
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Usuario> user = serviceUsuarios.findUserByUsername(username);
		Usuario userAuth = null;
		Collection<GrantedAuthority> grants = new ArrayList<GrantedAuthority>();
		userAuth = user.get(0);
		userAuth.getRoles().stream().forEach(rol -> grants.add(new SimpleGrantedAuthority(rol.getRol())));
		
		return new User(userAuth.getUsername(), userAuth.getPassword(), grants);
	}
	
}
