package com.tomfanhm.security.jwt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tomfanhm.security.jwt.model.User;
import com.tomfanhm.security.jwt.repository.UserRepository;

@Service
public class UserDetailsServiceImplement implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username).or(() -> userRepository.findByUsername(username))
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found."));

		return UserDetailsImplement.build(user);
	}
}