package com.example.controller;

import javax.validation.Valid;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Comic;
import com.example.repository.AuthorRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.ComicRepository;
import com.example.utils.ResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/api/comic")
public class ComicController {

	@Autowired
	ComicRepository comicRepository;
	
	@Autowired
	AuthorRepository authorRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getAll() {
		return ResponseUtil.returnListComic(comicRepository.findAll()).toString();
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String findById(@PathVariable("id") Long id) {
		return ResponseUtil.returnComic(
						comicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id)))
				.toString();
	}
	
  @PostMapping(value = "/crawler", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public String crawlerInsertALL(@RequestParam("url") String url) {
	  ObjectNode node = new ObjectMapper().createObjectNode();
  	try {
		Document document = Jsoup.connect(url).get();
		Element div = document.getElementsByClass("col-info-desc").get(0);
		
		node.put("title", div.getElementsByClass("title").get(0).text());
		
		node.put("description", div.getElementsByClass("desc-text").get(0).text());
		
		Elements authorLinks = div.select("div.info > div").get(0).getElementsByTag("a");
		ArrayNode arrayNodeAuthor = new ObjectMapper().createArrayNode();
		for (Element link : authorLinks) {
			ObjectNode authorNode = new ObjectMapper().createObjectNode();
			authorNode.put("id", authorRepository.findByFullName(link.text()).getId());
			arrayNodeAuthor.add(authorNode);
		}
		node.set("authors", arrayNodeAuthor);
		
		Elements categorieLinks = div.select("div.info > div").get(1).getElementsByTag("a");
		ArrayNode arrayNodeCategory = new ObjectMapper().createArrayNode();
		for (Element link : categorieLinks) {
			ObjectNode catNode = new ObjectMapper().createObjectNode();
			catNode.put("id", categoryRepository.findByName(link.text()).getId());
			arrayNodeCategory.add(catNode);
		}
		node.set("categories",arrayNodeCategory);
		
//		Elements status = div.select("div.info > div").get(3).getElementsByClass("text-primary");

		node.put("status", 1);
	} catch (Exception e) {
		return "";
	}
//  	Comic comic = new ObjectMapper().convertValue(node, Comic.class);
//  	return ResponseUtil.returnComic(comic).toString();
  	return node.toString();
  }

	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insert(@Valid @RequestBody Comic comic) {
		comic = comicRepository.save(comic);
		return ResponseUtil.returnComic(comic).toString();
	}

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String update(@PathVariable("id") Long id, @Valid @RequestBody Comic comicDetail) {
		Comic comic = comicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id));
		comic.setTitle(comicDetail.getTitle());
		comic.setDescription(comicDetail.getDescription());
		comic.setAuthors(comicDetail.getAuthors());
		comic.setCategories(comicDetail.getCategories());
		comic.setTags(comicDetail.getTags());
		comic.setChapters(comic.getChapters());
		comic.setStatus(comicDetail.getStatus());
		return ResponseUtil.returnComic(comic).toString();
	}

	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String delete(@PathVariable("id") Long id) {
		Comic comic = comicRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("author", "id", id));
		
		try {
			comicRepository.delete(comic);
			return "success";
		} catch (Exception e) {
			return "error";
		}
	}
	
}
