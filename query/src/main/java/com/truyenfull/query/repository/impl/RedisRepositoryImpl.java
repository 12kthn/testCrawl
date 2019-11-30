package com.truyenfull.query.repository.impl;

import com.truyenfull.lib.PageInfo;
import com.truyenfull.query.model.Chapter;
import com.truyenfull.query.model.Comic;
import com.truyenfull.query.repository.RedisRepository;
import com.truyenfull.query.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Boolean hasComic(String key, String comicUrlName) {
        return redisTemplate.opsForHash().hasKey(key, comicUrlName);
    }

    //Them danh sach truyen vao redis
    @Override
    public void addListComic(String key, List<Comic> comics) {
        try {
            redisTemplate.delete(key);

            for (Comic comic: comics) {
                String comicUrlName = comic.getUrlName();
                System.out.println(comicUrlName);

                redisTemplate.delete(comicUrlName);
                for (Chapter chapter: comic.getChapters()){
                    //list chapter(title, urlName) lưu dạng list dùng cho phân trang
                    redisTemplate.opsForList().rightPush(comicUrlName + "_Chapter_Pageable", ResponseUtil.returnChapterTitleAndUrlName(chapter).toString());

                    //list chapter(title, content) lưu dạng hash để hiển thị nội dung
                    redisTemplate.opsForHash().put(comicUrlName + "_Chapter_Content", chapter.getUrlName(), ResponseUtil.returnChapterTitleAndContent(chapter).toString());
                }

                //Lưu thông tin truyện với 50 chapter đầu tiên
                redisTemplate.opsForHash().put(key, comicUrlName, ResponseUtil.returnComic(comic).toString());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateComic(String key, Comic comic) {
        try {
            redisTemplate.opsForHash().put(key, comic.getUrlName(), ResponseUtil.returnComic(comic).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String findOneComic(String key, String comicUrlName) {
        return (String) redisTemplate.opsForHash().get(key, comicUrlName);
    }

    @Override
    public String findOneChapter(String comicUrlName, String chapterUrlName) {
        return (String) redisTemplate.opsForHash().get(comicUrlName + "_Chapter_Content", chapterUrlName);
    }

    @Override
    public String findListChapter(String comicUrlName, PageInfo pageInfo) {
        long from = (pageInfo.getPage() - 1)*pageInfo.getMaxPageItems();
        long to = (pageInfo.getPage())*pageInfo.getMaxPageItems() - 1;
        return String.valueOf(redisTemplate.opsForList().range(comicUrlName + "_Chapter_Pageable", from, to));
    }

}
