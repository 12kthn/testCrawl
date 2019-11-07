package com.example.services;

import java.io.IOException;
import java.util.List;

import com.example.model.Category;
import com.example.model.Comic;

public interface CrawlerService {
	
	public List<Category> saveListCategories(String url) throws IOException;
	
	public Comic getOneComic(String comicUrl) throws IOException;
	
	public Comic saveOneComic(String comicUrl) throws IOException;
	
	public List<Comic> saveListComics(String url) throws IOException;
	
}
