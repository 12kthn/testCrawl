package com.example.controller;

import javax.validation.Valid;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.ResourceNotFoundException;
import com.example.model.Category;
import com.example.repository.CategoryRepository;
import com.example.utils.ResponseUtil;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
	@Autowired
    CategoryRepository categoryRepository;
	
    // Get All category
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAll() {
    	return ResponseUtil.returnListCategory(categoryRepository.findAll()).toString();
    }
    
    @GetMapping(value = "/crawler")
    public boolean crawler() {
    	try {
			Document document = Jsoup.connect("https://truyenfull.vn/").get();
			System.out.println("Title: " + document.title());
			Elements categories = document.select("div.dropdown-menu > div.row > div.col-md-4 > ul > li > a");
			for (Element category : categories) {
				System.out.println("Category name: " + category.text());
				System.out.println("Link: " + category.attr("href"));
			}
		} catch (Exception e) {
			return false;
		}
    	return true;
    }
    
//    @PostMapping(value = "/crawler/insertAll", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public String crawlerInsertALL() {
//    	try {
//			Document document = Jsoup.connect("https://truyenfull.vn/").get();
//			Elements categories = document.select("div.dropdown-menu > div.row > div.col-md-4 > ul > li > a");
//			for (Element element : categories) {
//				System.out.println(element.text());
//				Category category = new Category(element.text());
//				categoryRepository.save(category);
//			}
//		} catch (Exception e) {
//			return "";
//		}
//    	return ResponseUtil.returnListCategory(categoryRepository.findAll()).toString();
//    }
    
    // Get a Single category
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Category getcategoryById(@PathVariable(value = "id") Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));
    }
    
    // Create a new category
    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Category createcategory(@Valid @RequestBody Category category) {
        return categoryRepository.save(category);
    }

    // Update a category
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Category updatecategory(@PathVariable(value = "id") Long categoryId,
            @Valid @RequestBody Category categoryDetails) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));

        category.setName(categoryDetails.getName());
        category.setComis(categoryDetails.getComis());

        Category updatedcategory = categoryRepository.save(category);
        return updatedcategory;
    }

    // Delete a category
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deletecategory(@PathVariable(value = "id") Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("category", "id", categoryId));

        categoryRepository.delete(category);

        return ResponseEntity.ok().build();
    }
}
