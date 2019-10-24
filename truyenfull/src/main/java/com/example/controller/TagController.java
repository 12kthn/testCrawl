package com.example.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Tag;
import com.example.repository.TagRepository;
import com.example.utils.ResponseUtil;


@RestController
@RequestMapping("/api/tag")
public class TagController {
	
	@Autowired
	TagRepository tagRepository;
	
	@GetMapping(produces = "application/json")
	public String findAll(){
		return ResponseUtil.returnListTag(tagRepository.findAll()).toString();
	}
	
	@GetMapping(value = "/tags/{id}", produces = "application/json")
	public String findById(@PathVariable("id") Long id){
		return ResponseUtil.returnTag(tagRepository.findById(id).get()).toString();
	}
	
	@PostMapping(value = "/tags", produces = "application/json")
    public String createTag(@Valid @RequestBody Tag tag) {
        return ResponseUtil.returnTag(tagRepository.save(tag)).toString();
    }
	
	@PutMapping(value = "/tags/{id}", produces = "application/json")
	public Tag updateTag(@PathVariable("id") Long tagId, @Valid @RequestBody Tag tagDetails) {
		Tag tag = tagRepository.findById(tagId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
		tag.setName(tagDetails.getName());
		tag.setComics(tagDetails.getComics());
		return tagRepository.save(tag);
	}
	
}
