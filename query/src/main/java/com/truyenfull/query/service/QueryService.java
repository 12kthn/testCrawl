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
import org.springframework.data.domain.Sort;
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
    public String getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            if (categories != null) {
                return ResponseUtil.success(ResponseUtil.returnListCategory(categories));
            } else {
                return ResponseUtil.notFound("Không tìm thấy danh sách thể loại");
            }
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getOneComic(String urlName) {
        Comic comic = comicRepository.findByUrlName(urlName);
        try {
            if (comic != null) {
                comic.setViews(comic.getViews() + 1);
                return ResponseUtil.success(ResponseUtil.returnComic(comic));
            } else {
                return ResponseUtil.notFound("Không tìm thấy truyện");
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
                    return ResponseUtil.notFound("Không tìm thấy danh sách chapter");
                }
                return ResponseUtil.success(ResponseUtil.returnListChaptersByComic(chapters));
            } else {
                return ResponseUtil.notFound("Không tìm thấy truyện");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String findComicByCategory(String categoryUrlName, PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("updateAt").descending());
        try {
            Category category = categoryRepository.findByUrlName(categoryUrlName);
            List<Comic> comics = comicRepository.findAllByCategories(category, pageable);
            if (comics.isEmpty()) {
                ResponseUtil.notFound("Khong tim thay truyen");
            }
            return ResponseUtil.success(ResponseUtil.returnListComicByCategory(comics));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String findComic(String key, PageInfo pageInfo){
        List<Comic> comics;
        switch (key) {
            case "truyen-moi":
                comics = findNewComic(pageInfo);
                break;
            case "truyen-hot":
                comics = findHotComic(pageInfo);
                break;
            case "truyen-full":
                comics = findComicByStatus("Full", pageInfo);
                break;
            case "tien-hiep-hay":
                comics = findHotComicByCategory("tien-hiep", pageInfo);
                break;
            case "kiem-hiep-hay":
                comics = findHotComicByCategory("kiem-hiep", pageInfo);
                break;
            case "truyen-teen-hay":
                comics = findHotComicByCategory("truyen-teen", pageInfo);
                break;
            case "ngon-tinh-hay":
                comics = findHotComicByCategory("ngon-tinh", pageInfo);
                break;
            case "dam-my-hay":
                comics = findHotComicByCategory("dam-my", pageInfo);
                break;
            default:
                return ResponseUtil.notFound("URL not found");
        }
        return ResponseUtil.success(ResponseUtil.returnListComicByCategory(comics));
    }

    private List<Comic> findNewComic(PageInfo pageInfo){
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("updateAt").descending());
        return comicRepository.findAll(pageable).getContent();
    }

    private List<Comic> findComicByStatus(String status, PageInfo pageInfo){
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("updateAt").descending());
        return comicRepository.findByStatus(status, pageable);
    }

    private List<Comic> findHotComic(PageInfo pageInfo){
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("views").descending().and(Sort.by("updateAt").descending()));
        return comicRepository.findAll(pageable).getContent();
    }

    private List<Comic> findHotComicByCategory(String categoryUrlName, PageInfo pageInfo){
        Category category = categoryRepository.findByUrlName(categoryUrlName);
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("views").descending().and(Sort.by("updateAt").descending()));
        return comicRepository.findAllByCategories(category, pageable);
    }

}
