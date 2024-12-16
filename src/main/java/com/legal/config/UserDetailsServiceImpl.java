package com.legal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.legal.dao.UserRepository;
import com.legal.entites.Users;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = userRepository.GetUserByUserName(username);
		
		if(user==null) {
			
			throw new UsernameNotFoundException("User with "+username+" not found");
			
		}
		
		UserDetailsImpl userDetailsImpl  = new UserDetailsImpl(user);
		
		return userDetailsImpl;
	}

}
