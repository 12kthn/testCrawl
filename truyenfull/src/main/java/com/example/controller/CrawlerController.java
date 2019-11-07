package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Category;
import com.example.model.Comic;
import com.example.services.CrawlerService;
import com.example.utils.ResponseUtil;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {

	@Autowired
	CrawlerService crawlerService;

	// insert truyenfull category
	@PostMapping(value = "/insertAllCategories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String crawlerInsertALL() throws IOException {
		String url = "https://truyenfull.vn/";
		List<Category> categories = crawlerService.saveListCategories(url);
		
		return ResponseUtil.returnListCategory(categories).toString();
	}

	@PostMapping(value = "/VietNamComic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String saveVietNamComics() throws IOException {
		String url = "https://truyenfull.vn/the-loai/viet-nam/";
		List<Comic> comics = crawlerService.saveListComics(url);
		
		return ResponseUtil.returnListComic(comics).toString();
	}

	// check 1 comic data
	@GetMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getData(@RequestParam("url") String comicUrl) throws IOException {
		Comic comic = crawlerService.getOneComic(comicUrl);
		return ResponseUtil.returnComic(comic).toString();
	}

	// Insert or update 1 comic
	@PostMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insertData(@RequestParam("url") String comicUrl) throws IOException {
		Comic comic = crawlerService.saveOneComic(comicUrl);
		return ResponseUtil.returnComic(comic).toString();
	}

	

}
