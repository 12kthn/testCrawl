package com.truyenfull.api.controller;

import com.truyenfull.api.config.CrawlerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class CrawlerController {

    @Autowired
    CrawlerClient client;

    @PostMapping(value = "/crawler/category", produces = "application/json")
	public String getAllCategories() {
		String response;
		try {
			response = client.crawlAllCategories();
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = "/crawler/comic", produces = "application/json")
	public String crawlOneComic(@RequestParam("url") String url){
    	String response;
		try {
			response = client.crawlOneComic(url);
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
		}
		return response;
	}

	@PostMapping(value = "/crawler/{categoryUrlName}", produces = "application/json")
	public String crawlByCategory(@PathVariable String categoryUrlName){
		String response;
		try {
			response = client.crawlByCategory(categoryUrlName);
		} catch (Exception e) {
			e.printStackTrace();
			response = e.getMessage();
		}
		return response;
	}

}
