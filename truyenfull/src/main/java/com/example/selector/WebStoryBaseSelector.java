package com.example.selector;

public interface WebStoryBaseSelector<T extends StoryContentBaseSelector, U extends CategoryContentBaseSelector> {

    String mainUrl();

    String getCategoryListSelector();

    String getStoryListSelector();

    String getNextStoryPageSelector();

    T getStoryContentSelector();

    U getCategoryContentSelector();
}
