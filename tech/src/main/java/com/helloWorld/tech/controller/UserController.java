package com.helloWorld.tech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@GetMapping
	public ResponseEntity<String> listUsers() {
		return ResponseEntity.ok("users");
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getUser(@PathVariable Long id) {
		return ResponseEntity.ok("user " + id);
	}
}
