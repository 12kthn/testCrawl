package com.truyenfull.query.service;

import com.truyenfull.lib.IQueryService;
import com.truyenfull.lib.PageInfo;
import com.truyenfull.query.model.Category;
import com.truyenfull.query.model.Chapter;
import com.truyenfull.query.model.Comic;
import com.truyenfull.query.repository.CategoryRepository;
import com.truyenfull.query.repository.ChapterRepository;
import com.truyenfull.query.repository.ComicRepository;
import com.truyenfull.query.utils.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService implements IQueryService.Iface {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ComicRepository comicRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Override
    public String getAllCategories(){
        return ResponseUtil.returnListCategory(categoryRepository.findAll()).toString();
    }

    @Override
    public String getOneComic(String urlName){
        Comic comic = comicRepository.findByUrlName(urlName);
        try {
            if (comic != null) {
                comic.setViews(comic.getViews() + 1);
                return ResponseUtil.returnComic(comic).toString();
            } else {
                throw new Exception("Khong tim thay truyen");
            }
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (comic != null) {
                comicRepository.save(comic);
            }
        }
    }

    @Override
    public String getComicChapters(String comicUrlName, PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems);
        try {
            Comic comic = comicRepository.findByUrlName(comicUrlName);
            if (comic != null) {
                List<Chapter> chapters = chapterRepository.findByComic(comic, pageable);
                if (chapters.isEmpty()) {
                    throw new Exception("Khong tim thay danh sach chapter");
                }
                return ResponseUtil.returnListChaptersByComic(chapters).toString();
            } else {
                throw new Exception("Khong tim thay truyen");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String findComicByCategory(String categoryUrlName, PageInfo pageInfo){
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems);
        try {
            Category category = categoryRepository.findByUrlName(categoryUrlName);
            List<Comic> comics = comicRepository.findAllByCategories(category, pageable);
            if (comics.isEmpty()) {
                throw new Exception("Khong tim thay truyen");
            }
            return ResponseUtil.returnListComicByCategory(comics).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
