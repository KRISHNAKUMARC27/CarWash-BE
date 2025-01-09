package com.sas.carwash.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.User;
import com.sas.carwash.model.AuthRequest;
import com.sas.carwash.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
	
	private final UserRepository userRepository;
    private final PasswordEncoder encoder;

	@PostMapping("/changePassword")
	public User changePassword(@RequestBody AuthRequest authRequest) {
		
		User origUser = userRepository.findByUsername(authRequest.getUsername());
		if(origUser != null) {
			origUser.setPassword(encoder.encode(authRequest.getPassword()));
			origUser = userRepository.save(origUser);
			System.out.println("Password changed");
		}
		return origUser;
	}

}
