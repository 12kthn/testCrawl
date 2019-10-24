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
import com.example.model.Author;
import com.example.repository.AuthorRepository;
import com.example.utils.ResponseUtil;

@RestController
@RequestMapping("/api/author")
public class AuthorController {
	@Autowired
	AuthorRepository authorRepository;

	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getAll() {
		return ResponseUtil.returnListAuthor(authorRepository.findAll()).toString();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String findById(@PathVariable("id") Long id) {
		return ResponseUtil.returnAuthor(
						authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id)))
				.toString();
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insert(@Valid @RequestBody Author author) {
		author = authorRepository.save(author);
		return ResponseUtil.returnAuthor(author).toString();
	}

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String update(@PathVariable("id") Long id, @Valid @RequestBody Author authorDetail) {
		Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id));
		author.setFullName(authorDetail.getFullName());
		author.setComics(authorDetail.getComics());
		return ResponseUtil.returnAuthor(author).toString();
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String delete(@PathVariable("id") Long id) {
		Author author = authorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id));
		
		try {
			authorRepository.delete(author);
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}
}
