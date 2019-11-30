package com.truyenfull.query.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truyenfull.lib.IQueryService;
import com.truyenfull.lib.PageInfo;
import com.truyenfull.query.model.Category;
import com.truyenfull.query.model.Chapter;
import com.truyenfull.query.model.Comic;
import com.truyenfull.query.repository.CategoryRepository;
import com.truyenfull.query.repository.ChapterRepository;
import com.truyenfull.query.repository.ComicRepository;
import com.truyenfull.query.repository.RedisRepository;
import com.truyenfull.query.utils.ObjectMapperSingleton;
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

    @Autowired
    RedisRepository redisRepository;

    private String KEY = "TopHotComic";
    private ObjectMapper mapper = ObjectMapperSingleton.getInstance();

    @Override
    public String getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            if (categories != null) {
                return ResponseUtil.success(ResponseUtil.returnListCategory(categories));
            } else {
                return ResponseUtil.notFound("Không tìm thấy danh sách thể loại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public String getOneComic(String urlName) {
        Comic comic = null;
        try {
            if (redisRepository.hasComic(KEY, urlName)) {
                System.out.println("comic is in redis");
                //convert chuỗi Json sang Object Comic
                comic = mapper.readValue(redisRepository.findOneComic(KEY, urlName), Comic.class);

                comic.setViews(comic.getViews() + 1);
                redisRepository.updateComic(KEY, comic);
            } else {
                System.out.println("comic isn't in redis");
                comic = comicRepository.findByUrlName(urlName);
                comic.setViews(comic.getViews() + 1);
            }

            if (comic != null) {
                return ResponseUtil.success(ResponseUtil.returnComic(comic));
            } else {
                return ResponseUtil.notFound("Không tìm thấy truyện");
            }
        } catch (NullPointerException e) {
            return ResponseUtil.notFound("Không tìm thấy truyện");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            comicRepository.updateViews(comic.getViews(), comic.getId());
        }
    }

    @Override
    public String getOneChapter(String comicUrlName, String chapterUrlName) {
        Chapter chapter;
        try {
            if (redisRepository.hasComic(KEY, comicUrlName)) {
                System.out.println("Chapters of comic is in redis");
                //convert chuỗi Json sang Object Chapter
                chapter = mapper.readValue(redisRepository.findOneChapter(comicUrlName, chapterUrlName), Chapter.class);
            } else {
                System.out.println("Chapters of comic is not in redis");
                chapter = chapterRepository.findByComicAndUrlName(comicUrlName, chapterUrlName);
            }
        } catch (NullPointerException e) {
            return ResponseUtil.notFound("Khong tim thay chapter");
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            Comic comic = comicRepository.findByUrlName(comicUrlName);
            comic.setViews(comic.getViews() + 1);
            comicRepository.updateViews(comic.getViews(), comic.getId());
        }

        if (chapter != null) {
            return ResponseUtil.success(ResponseUtil.returnChapterTitleAndContent(chapter));
        } else {
            return ResponseUtil.notFound("Khong tim thay chapter");
        }

    }

    @Override
    public String getComicChapters(String comicUrlName, PageInfo pageInfo) {
        List<Chapter> chapters;
        try {
            if (redisRepository.hasComic(KEY, comicUrlName)) {
                System.out.println("Chapters of comic is in redis");
                //convert chuỗi Json sang List<Chapter>
                chapters = mapper.readValue(
                        redisRepository.findListChapter(comicUrlName, pageInfo),
                        mapper.getTypeFactory().constructCollectionType(List.class, Chapter.class));
            } else {
                System.out.println("Chapters of comic isn't in redis");
                Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems);
                chapters = chapterRepository.findByComic(comicUrlName, pageable);
            }
        } catch (NullPointerException e) {
            return ResponseUtil.notFound("Không tìm thấy danh sách chapter");
        } catch (Exception e) {
            return e.getMessage();
        } finally {
            Comic comic = comicRepository.findByUrlName(comicUrlName);
            comic.setViews(comic.getViews() + 1);
            comicRepository.updateViews(comic.getViews(), comic.getId());
        }
        if (chapters.isEmpty()) {
            return ResponseUtil.notFound("Không tìm thấy danh sách chapter");
        } else {
            return ResponseUtil.success(ResponseUtil.returnListChapters(chapters));
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
    public String findComic(String key, PageInfo pageInfo) {
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

    @Override
    public void addTopHotComic(long numberComics){
        PageInfo pageInfo = new PageInfo(1, (int) numberComics);
        redisRepository.addListComic(KEY, findHotComic(pageInfo));
    }

    private List<Comic> findNewComic(PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("updateAt").descending());
        return comicRepository.findAll(pageable).getContent();
    }

    private List<Comic> findComicByStatus(String status, PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("updateAt").descending());
        return comicRepository.findByStatus(status, pageable);
    }

    private List<Comic> findHotComic(PageInfo pageInfo) {
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("views").descending().and(Sort.by("updateAt").descending()));
        return comicRepository.findAll(pageable).getContent();
    }

    private List<Comic> findHotComicByCategory(String categoryUrlName, PageInfo pageInfo) {
        Category category = categoryRepository.findByUrlName(categoryUrlName);
        Pageable pageable = PageRequest.of(pageInfo.getPage() - 1, pageInfo.maxPageItems,
                Sort.by("views").descending().and(Sort.by("updateAt").descending()));
        return comicRepository.findAllByCategories(category, pageable);
    }

}
