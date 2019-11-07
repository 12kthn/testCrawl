package com.example.selector.impl;

import com.example.selector.WebStoryBaseSelector;

public class TruyenFullSelector implements
        WebStoryBaseSelector<TruyenFullStorySelector, TruyenFullCategorySelector> {

    @Override
    public String mainUrl() {
        return "https://truyenfull.vn/danh-sach/truyen-moi/";
    }

    @Override
    public String getCategoryListSelector() {
        return ".list-cat .row a";
    }

    @Override
    public String getStoryListSelector() {
        return "div[class=row][itemscope] a[itemprop=url]";
    }

    @Override
    public String getNextStoryPageSelector() {
        return ".pagination li[class=active] + li > a";
    }

    @Override
    public TruyenFullStorySelector getStoryContentSelector() {
        return TruyenFullStorySelector.getInstance();
    }

    @Override
    public TruyenFullCategorySelector getCategoryContentSelector() {
        return TruyenFullCategorySelector.getInstance();
    }

}
