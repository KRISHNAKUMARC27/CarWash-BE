package com.sas.carwash.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sas.carwash.entity.Role;
import com.sas.carwash.entity.User;
import com.sas.carwash.model.AuthRequest;
import com.sas.carwash.repository.RoleRepository;
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
	private final RoleRepository roleRepository;

	@PostMapping("/changePassword")
	public User changePassword(@RequestBody AuthRequest authRequest) {

		User origUser = userRepository.findByUsername(authRequest.getUsername());
		if (origUser != null) {
			origUser.setPassword(encoder.encode(authRequest.getPassword()));
			origUser = userRepository.save(origUser);
			System.out.println("Password changed");
		}
		return origUser;
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody User user) {
		try {
			User currentUser = userRepository.findByUsername(user.getUsername());
			if (currentUser != null) {
				throw new Exception("Username already exists");
			}
			user.setPassword(encoder.encode(user.getPassword()));
			return ResponseEntity.ok().body(userRepository.save(user));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/createRole")
	public Role createRole(@RequestBody Role role) {
		return roleRepository.save(role);
	}

	@GetMapping("/role/{name}")
	public Role getRole(@PathVariable String name) {
		return roleRepository.findByName(name);
	}
	
	@GetMapping("/role")
	public List<Role> findAllRoles() {
		return roleRepository.findAll();
	}
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		List<Role> roleList = roleRepository.findAll();
		Set<String> roleSet = roleList.stream().map(Role::getName).collect(Collectors.toSet());
		
		Set<String> mainRoleSet = new HashSet<>(Set.of("ADMIN","MANAGER","INVOICE","ESTIMATE"));
		mainRoleSet.removeAll(roleSet);
		
		if(!mainRoleSet.isEmpty()) {
			mainRoleSet.stream().forEach(role -> {
				Role r = Role.builder().name(role).build();
				roleRepository.save(r);
			});
		}
		
	}


}
