package com.truyenfull.query.repository;

import com.truyenfull.lib.PageInfo;
import com.truyenfull.query.model.Comic;

import java.util.List;

public interface RedisRepository {

    Boolean hasComic(String key, String comicUrlName);

    void addListComic(String key, List<Comic> comics);

    void updateComic(String key, Comic comic);

    String findOneComic(String key, String comicUrlName);

    String findOneChapter(String comicUrlname, String chapterUrlName);

    String findListChapter(String comicUrlName, PageInfo pageInfo);

}
