package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
	
	@RequestMapping
	public String index() {
		return "WELLCOME TO TRUYENFULL.FAKE";
	}
	
}
