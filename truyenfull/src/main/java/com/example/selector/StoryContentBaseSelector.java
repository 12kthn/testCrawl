package com.example.selector;

public interface StoryContentBaseSelector<T extends ChapterContentBaseSelector> {

    String title();

    String author();

    String categorieLinks();

    String description();

    String chapterLinks();
    
    String totalPage();

	String status_full();
}
