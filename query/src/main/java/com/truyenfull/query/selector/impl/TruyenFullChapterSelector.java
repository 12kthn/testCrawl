package com.truyenfull.query.selector.impl;


import com.truyenfull.query.selector.ChapterContentBaseSelector;

public class TruyenFullChapterSelector implements ChapterContentBaseSelector {

    private TruyenFullChapterSelector() {

    }

    private static class SingletonHelper {

        private static final TruyenFullChapterSelector INSTANCE = new TruyenFullChapterSelector();
    }

    public static TruyenFullChapterSelector getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public String title() {
        return "a.chapter-title";
    }

    @Override
    public String content() {
        return "#chapter-c";
    }
}
