package com.truyenfull.api.controller;

import com.truyenfull.api.config.QueryClient;
import com.truyenfull.lib.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
	public String getComicsByCategory(@PathVariable String categoryUrlName, @RequestBody PageInfo pageInfo) {
		String response;
		try {
			pageInfo.setPage(1);
			response = client.findComicByCategory(categoryUrlName, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}


	@GetMapping(value = "/the-loai/{categoryUrlName}/trang-{page}", produces = "application/json")
	public String getComicsByCategory(@PathVariable String categoryUrlName,
									  @PathVariable int page,
									  @RequestBody PageInfo pageInfo) {
		String response;
		try {
			pageInfo.setPage(page);
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
	public String getComicChapters(@PathVariable String comicUrlName,
								   @PathVariable int page,
								   @RequestBody PageInfo pageInfo) {
		String response;
		try {
			pageInfo.setPage(page);
			response = client.getComicChapters(comicUrlName, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/{comicUrlName}/{chapterUrlName}", produces = "application/json")
	public String getOneChapter(@PathVariable String comicUrlName, @PathVariable String chapterUrlName) {
		String response;
		try {
			response = client.getOneChapter(comicUrlName, chapterUrlName);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/danh-sach/{key}", produces = "application/json")
	public String getChaptersByKey(@PathVariable String key, @RequestBody PageInfo pageInfo) {
		String response;
		pageInfo.setPage(1);
		try {
			response = client.findComic(key, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping(value = "/danh-sach/{key}/trang-{page}", produces = "application/json")
	public String getChaptersByKey(@PathVariable String key,
								   @PathVariable int page,
								   @RequestBody PageInfo pageInfo) {
		String response;
		try {
			pageInfo.setPage(page);
			response = client.findComic(key, pageInfo);
		} catch (Exception e) {
			response = e.getMessage();
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping(value = "/addTopHotComic")
	public String addTopHotComic(@RequestParam("numberComics") long numberComics){
		try {
			client.addTopHotComic(numberComics);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
