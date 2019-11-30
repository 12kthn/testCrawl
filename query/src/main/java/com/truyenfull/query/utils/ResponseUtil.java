package com.truyenfull.query.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.truyenfull.query.constant.StatusCode;
import com.truyenfull.query.model.Author;
import com.truyenfull.query.model.Category;
import com.truyenfull.query.model.Chapter;
import com.truyenfull.query.model.Comic;

import java.util.ArrayList;
import java.util.List;

public class ResponseUtil {

    private static ObjectMapper mapper = ObjectMapperSingleton.getInstance();

    public static String success(ContainerNode body){
        ObjectNode node = mapper.createObjectNode();
        node.put(StatusCode.class.getSimpleName(), StatusCode.SUCCESS.getValue());
        node.put("Message", StatusCode.SUCCESS.name());
        node.set("Response", body);
        return node.toString();
    }

    public static String notFound(String body){
        ObjectNode node = mapper.createObjectNode();
        node.put(StatusCode.class.getSimpleName(), StatusCode.NOT_FOUND.getValue());
        node.put("Error", StatusCode.NOT_FOUND.name());
        node.put("Response", body);
        return node.toString();
    }

    public static ArrayNode returnListCategory(List<Category> categories) {
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Category category : categories) {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", category.getName());
            node.put("urlName", category.getUrlName());
            arrayNode.add(node);
        }
        return arrayNode;
    }

    public static ObjectNode returnComic(Comic comic) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", comic.getId());
        node.put("title", comic.getTitle());
        node.put("description", comic.getDescription());
        node.put("status", comic.getStatus());
        node.put("rating", comic.getRating());
        node.put("views", comic.getViews());
        node.put("urlName", comic.getUrlName());

        setCategoriesFieldForComic(comic, node);
        setAuthorsFieldForComic(comic, node);
        setChaptersFieldForComic(comic, node);

//        node.put("createAt", TimeUtil.toString(comic.getCreateAt()));
//        node.put("updateAt", TimeUtil.toString(comic.getUpdateAt()));
        return node;
    }

    public static ArrayNode returnListComicByCategory(List<Comic> comics) {
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Comic comic: comics) {
            ObjectNode node = mapper.createObjectNode();
            node.put("title", comic.getTitle());
            node.put("urlName", comic.getUrlName());
            setAuthorsFieldForComic(comic, node);
            try {
                List<Chapter> chapters = comic.getChapters();
                node.put("lastChapterTitle", chapters.get(chapters.size() - 1).getTitle());
            } catch (Exception e){
                e.printStackTrace();
            }

            arrayNode.add(node);
        }
        return arrayNode;
    }

    public static ObjectNode returnChapterTitleAndContent(Chapter chapter) {
        ObjectNode node = mapper.createObjectNode();
        node.put("title", chapter.getTitle());
        node.put("content", chapter.getContent());
        return node;
    }

    public static ObjectNode returnChapterTitleAndUrlName(Chapter chapter) {
        ObjectNode node = mapper.createObjectNode();
        node.put("title", chapter.getTitle());
        node.put("urlName", chapter.getUrlName());
        return node;
    }

    public static ArrayNode returnListChapters(List<Chapter> chapters) {
        ArrayNode arrayNode = mapper.createArrayNode();
        for (Chapter chapter: chapters) {
            arrayNode.add(returnChapterTitleAndUrlName(chapter));
        }
        return arrayNode;
    }

    private static void setAuthorsFieldForComic(Comic comic, ObjectNode node) {
        if (comic.getAuthors() != null) {
            ArrayNode arrayAuthor = mapper.createArrayNode();
            for (Author author: comic.getAuthors()) {
                ObjectNode categoryNode = mapper.createObjectNode();
                categoryNode.put("fullName", author.getFullName());
                arrayAuthor.add(categoryNode);
            }
            node.set("authors", arrayAuthor);
        }
    }

    private static void setCategoriesFieldForComic(Comic comic, ObjectNode node) {
        if (comic.getCategories() != null) {
            ArrayNode arrayCategory = mapper.createArrayNode();
            for (Category category: comic.getCategories()) {
                ObjectNode categoryNode = mapper.createObjectNode();
                categoryNode.put("name", category.getName());
                arrayCategory.add(categoryNode);
            }
            node.set("categories", arrayCategory);
        }
    }

    //Lay 50 chapter đầu
    private static void setChaptersFieldForComic(Comic comic, ObjectNode node) {
        List<Chapter> chapters = comic.getChapters();
        if (chapters.size() > 0 && chapters.size() < 50){
            node.set("chapters", returnListChapters(chapters));
        } else {
            node.set("chapters", returnListChapters(comic.getChapters().subList(0, 50)));
        }
    }

}
