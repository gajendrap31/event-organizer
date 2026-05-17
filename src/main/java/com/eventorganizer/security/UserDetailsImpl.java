package com.eventorganizer.security;

import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;
		
	@JsonIgnore
	private String password;
	
	private String email;

	private String role;
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String getUsername() {
		return email;
	}
	
	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {

		return password;
	}
	
	public String getRole() {
		return role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return Collections.singleton(new SimpleGrantedAuthority(role));
	}

	public UserDetailsImpl(int id, String name, String password, String email, String role) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
		this.role = role;
	}
}
