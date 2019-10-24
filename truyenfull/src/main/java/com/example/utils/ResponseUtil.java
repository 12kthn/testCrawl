package com.example.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.model.Author;
import com.example.model.Category;
import com.example.model.Chapter;
import com.example.model.Comic;
import com.example.model.Tag;
import com.example.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.example.utils.TimeUtil;

public class ResponseUtil {
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static ObjectNode returnUser(User user) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", user.getId());
		node.put("userName", user.getUserName());
		node.put("password", user.getPassword());
		node.put("role", user.getRole());
		node.put("createAt", TimeUtil.toString(user.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(user.getUpdateAt()));
		return node;
	}
	
	public static ArrayNode returnListUser(List<User> users) {
		ArrayNode arrayNode = mapper.createArrayNode();
        for (User user : users) {
            arrayNode.add(returnUser(user));
        }
        return arrayNode;
	}
	
	public static ObjectNode returnAuthor(Author author) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", author.getId());
		node.put("fullName", author.getFullName());
		List<String> comicTitles = new ArrayList<>();
		for (Comic comic : author.getComics()) {
			comicTitles.add(comic.getTitle());
		}
		node.put("comis", comicTitles.toString());
		node.put("createAt", TimeUtil.toString(author.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(author.getUpdateAt()));
		return node;
	}
	
	public static ArrayNode returnListAuthor(List<Author> authors) {
		ArrayNode arrayNode = mapper.createArrayNode();
        for (Author author : authors) {
        	arrayNode.add(returnAuthor(author));
        }
        return arrayNode;
	}
	
	public static ObjectNode returnChapter(Chapter chapter) {
		ObjectNode node = mapper.createObjectNode();
		node.put("id", chapter.getId());
		node.put("title", chapter.getTitle());
		node.put("content", chapter.getContent());
		node.put("comicTitle", chapter.getComic().getTitle());
		node.put("createAt", TimeUtil.toString(chapter.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(chapter.getUpdateAt()));
		return node;
	}
	
	public static ArrayNode returnListChapter(List<Chapter> chapters) {
		ArrayNode arrayNode = mapper.createArrayNode();
        for (Chapter chapter : chapters) {
        	arrayNode.add(returnChapter(chapter));
        }
        return arrayNode;
	}
	
	public static ObjectNode returnCategory(Category category) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", category.getId());
        node.put("name", category.getName());
        if (category.getComis() != null) {
        	List<String> comicTitles = new ArrayList<>();
            for (Comic comic: category.getComis()) {
            	comicTitles.add(comic.getTitle());
            }
            node.put("comics", comicTitles.toString());
		}
        node.put("createAt", TimeUtil.toString(category.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(category.getUpdateAt()));
        return node;
    }

    public static ArrayNode returnListCategory(List<Category> categorys) {
        ArrayNode node = mapper.createArrayNode();
        for (Category category : categorys) {
            node.add((returnCategory(category)));
        }
        return node;
    }
    
    public static ObjectNode returnTag(Tag tag) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", tag.getId());
        node.put("name", tag.getName());
        List<String> comicTitles = new ArrayList<>();
        for (Comic comic: tag.getComics()) {
        	comicTitles.add(comic.getTitle());
        }
        node.put("tags", comicTitles.toString());
        node.put("createAt", TimeUtil.toString(tag.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(tag.getUpdateAt()));
        return node;
    }
    
    public static ArrayNode returnListTag(List<Tag> tags) {
        ArrayNode node = mapper.createArrayNode();
        for (Tag tag : tags) {
            node.add(returnTag(tag));
        }
        return node;
    }
    
    public static ObjectNode returnComic(Comic comic) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", comic.getId());
        node.put("title", comic.getTitle());
        node.put("description", comic.getDescription());
        node.put("status", comic.getStatus());
        
        List<String> categoryTitles = new ArrayList<>();
        for (Category category: comic.getCategories()) {
        	categoryTitles.add(category.getName());
        }
        node.put("categorys", categoryTitles.toString());
        
        if (comic.getTags() != null) {
        	List<String> tagNames = new ArrayList<>();
            for (Tag tag: comic.getTags()) {
            	tagNames.add(tag.getName());
            }
            node.put("tags", tagNames.toString());
		}
        
        if (comic.getAuthors() != null) {
	        List<String> authorNames = new ArrayList<>();
	        for (Author author: comic.getAuthors()) {
	        	authorNames.add(author.getFullName());
	        }
	        node.put("authors", authorNames.toString());
        }
        
        if (comic.getChapters() != null) {
	        List<String> chapterTitles = new ArrayList<>();
	        for (Chapter chapter: comic.getChapters()) {
	        	chapterTitles.add(chapter.getTitle());
	        }
	        node.put("chapters", chapterTitles.toString());
        }
        
        node.put("createAt", TimeUtil.toString(comic.getCreateAt()));
		node.put("updateAt", TimeUtil.toString(comic.getUpdateAt()));
        return node;
    }
    
    public static ArrayNode returnListComic(List<Comic> comics) {
        ArrayNode node = mapper.createArrayNode();
        for (Comic comic : comics) {
            node.add(returnComic(comic));
        }
        return node;
    }
}
