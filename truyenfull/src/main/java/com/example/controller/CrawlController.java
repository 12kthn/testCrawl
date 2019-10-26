package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Author;
import com.example.model.Category;
import com.example.model.Chapter;
import com.example.model.Comic;
import com.example.repository.AuthorRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.ComicRepository;
import com.example.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/crawler")
public class CrawlController {
	
	@Autowired
	ComicRepository comicRepository;
	
	@Autowired
	AuthorRepository authorRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@PostMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insertData(@RequestParam("url") String url) {
		Comic comic = new Comic();
		try {
			Document document = Jsoup.connect(url).get();
			List<String> allChapterLinks = getAllListChapterLinks(document, getAllPaginationLinks(document, url));
			comic = getObject(comic, document, allChapterLinks);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return comicRepository.save(comic).toString();
	}
	
	//check data
	@GetMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getData(@RequestParam("url") String url) {
		Comic comic = new Comic();
		try {
			Document document = Jsoup.connect(url).get();
			List<String> allChapterLinks = getAllListChapterLinks(document, getAllPaginationLinks(document, url));
			comic = getObject(comic, document, allChapterLinks);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return ResponseUtil.returnComic(comic).toString();
	}
	
	private Comic getObject(Comic comic, Document document, List<String> allChapterLinks) {
		
		Element inforDiv = document.getElementsByClass("col-info-desc").get(0);

		comic.setTitle(inforDiv.getElementsByClass("title").get(0).text());
		comic.setDescription(inforDiv.getElementsByClass("desc-text").get(0).text());

		Elements authorLinks = inforDiv.select("div.info > div").get(0).getElementsByTag("a");
		List<Author> authors = new ArrayList<>(); 
		for (Element link : authorLinks) {
			Author author = authorRepository.findByFullName(link.text());
			if (author == null) {
				author = authorRepository.save(new Author(link.text()));
			}
			authors.add(author);
		}
		comic.setAuthors(authors);

		Elements categorieLinks = inforDiv.select("div.info > div").get(1).getElementsByTag("a");
		List<Category> categories = new ArrayList<>();
		for (Element link : categorieLinks) {
			Category category = categoryRepository.findByName(link.text());
			categories.add(category);
		}
		comic.setCategories(categories);

		comic.setStatus(inforDiv.select("div.info > div").get(3).getElementsByClass("text-primary").text());
		
		for (String chapterUrl : allChapterLinks) {
			Chapter chapter = new Chapter();
			try {
				document = Jsoup.connect(chapterUrl).get();
				chapter.setTitle(document.select("a.chapter-title").get(0).text());
				chapter.setContent(document.getElementById("chapter-c").text());
				comic.addChapter(chapter);
			} catch (IOException e) {
				e.printStackTrace();
				chapter = null;
			}
			
		}
		
		return comic;
	}
	
	private List<String> getAllListChapterLinks(Document document, List<String> paginationlinks){
		List<String> allChapterLinks = new ArrayList<>();
		try {
			for(String paginationlink : paginationlinks) {
				document = Jsoup.connect(paginationlink).get();
				Elements chapterLinks = document.select("ul.list-chapter > li > a");
				for (Element link : chapterLinks) {
					allChapterLinks.add(link.attr("href"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return allChapterLinks;
	}
	
	private List<String> getAllPaginationLinks(Document document, String url){
		List<String> list = new ArrayList<>();
		String paginationLink;
		int totalPage = Integer.valueOf(document.getElementById("total-page").val());
		for (int i = 0; i < totalPage; i++) {
			paginationLink = url + "trang-" + (i+1) + "/#list-chapter";
			list.add(paginationLink);
		}
		return list;
	}

}
