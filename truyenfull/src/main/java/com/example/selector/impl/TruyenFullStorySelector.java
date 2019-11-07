package com.example.selector.impl;

import com.example.selector.StoryContentBaseSelector;

public class TruyenFullStorySelector implements StoryContentBaseSelector<TruyenFullChapterSelector> {

    private TruyenFullStorySelector() {

    }

    private static class SingletonHelper {

        private static final TruyenFullStorySelector INSTANCE = new TruyenFullStorySelector();
    }

    public static TruyenFullStorySelector getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public String title() {
        return "div.col-info-desc h3.title";
    }

    @Override
    public String description() {
        return "div.col-info-desc div.desc-text";
    }
    
    @Override
    public String author() {
        return "div.col-info-desc div.info a[itemprop='author']";
    }

    @Override
    public String categorieLinks() {
        return "div.col-info-desc div.info a[itemprop='genre']";
    }

    @Override
    public String status_full() {
        return "div.col-info-desc div.info span.text-success";
    }
    

	@Override
	public String chapterLinks() {
		return "ul.list-chapter > li > a";
	}

	@Override
	public String totalPage() {
		return "#total-page";
	}

}
