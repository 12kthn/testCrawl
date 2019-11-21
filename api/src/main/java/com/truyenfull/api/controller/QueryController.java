package com.truyenfull.api.controller;

import com.truyenfull.api.config.QueryClient;
import com.truyenfull.lib.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class QueryController {
	
	@Autowired
    QueryClient client;

	@GetMapping(value = "/category", produces = "application/json")
	public String getAllCategories() {
		String response;
		try {
			response = client.getAllCategories();
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/the-loai/{categoryUrlName}", produces = "application/json")
	public String getComicsByCategory(@PathVariable String categoryUrlName) {
		String response;
		try {
			PageInfo pageInfo = new PageInfo(1,10);
			response = client.findComicByCategory(categoryUrlName, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/the-loai/{categoryUrlName}/trang-{page}", produces = "application/json")
	public String getComicsByCategory(@PathVariable String categoryUrlName, @PathVariable int page) {
		String response;
		try {
			PageInfo pageInfo = new PageInfo(page,10);
			response = client.findComicByCategory(categoryUrlName, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/{urlName}", produces = "application/json")
	public String getOneComic(@PathVariable String urlName) {
		String response;
		try {
			response = client.getOneComic(urlName);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/{comicUrlName}/trang-{page}", produces = "application/json")
	public String getComicChapters(@PathVariable String comicUrlName, @PathVariable int page) {
		String response;
		try {
			PageInfo pageInfo = new PageInfo(page,10);
			response = client.getComicChapters(comicUrlName, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

}
