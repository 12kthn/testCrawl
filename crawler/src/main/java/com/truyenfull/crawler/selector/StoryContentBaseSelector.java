package com.truyenfull.crawler.selector;

public interface StoryContentBaseSelector<T extends ChapterContentBaseSelector> {

    String title();

    String author();

    String categoryLinks();

    String description();

    String chapterLinks();
    
    String totalPage();

	String status_full();

	String rating();
}
