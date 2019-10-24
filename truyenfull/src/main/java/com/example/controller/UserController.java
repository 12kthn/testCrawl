package com.example.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ResourceNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.utils.ResponseUtil;

@RestController
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getAll() {
		return ResponseUtil.returnListUser(userRepository.findAll()).toString();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String findById(@PathVariable("id") Long id) {
		return ResponseUtil
				.returnUser(
						userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id)))
				.toString();
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insert(@Valid @RequestBody User user) {
		user = userRepository.save(user);
		return ResponseUtil.returnUser(user).toString();
	}

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String update(@PathVariable("id") Long id, @Valid @RequestBody User userDetail) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		user.setPassword(userDetail.getPassword());
		user.setUserName(userDetail.getUserName());
		user.setRole(userDetail.getRole());
		return ResponseUtil.returnUser(user).toString();
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String delete(@PathVariable("id") Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
		
		try {
			userRepository.delete(user);
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}

}
