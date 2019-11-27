package com.truyenfull.crawler.service;

import com.truyenfull.crawler.model.Author;
import com.truyenfull.crawler.model.Category;
import com.truyenfull.crawler.model.Chapter;
import com.truyenfull.crawler.model.Comic;
import com.truyenfull.crawler.repository.AuthorRepository;
import com.truyenfull.crawler.repository.CategoryRepository;
import com.truyenfull.crawler.repository.ComicRepository;
import com.truyenfull.crawler.selector.impl.TruyenFullCategorySelector;
import com.truyenfull.crawler.selector.impl.TruyenFullChapterSelector;
import com.truyenfull.crawler.selector.impl.TruyenFullStorySelector;
import com.truyenfull.lib.ICrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CrawlerService implements ICrawlerService.Iface {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ComicRepository comicRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public String crawlAllCategories(){
        TruyenFullCategorySelector categorySelector = TruyenFullCategorySelector.getInstance();
        List<Category> categories = new ArrayList<>();
        try {
            Document document = Jsoup.connect("https://truyenfull.vn/").get();
            Elements categoryLinks = document.select(categorySelector.name());
            for (Element element : categoryLinks) {
                String[] urlPath = element.attr("href").split("/");
                categories.add(new Category(element.text(), urlPath[urlPath.length - 1]));
            }
            categoryRepository.saveAll(categories);
            return "crawl thanh cong";
        } catch (Exception e) {
            e.printStackTrace();
            return "crawl that bai";
        }
    }

    @Override
    public String crawlOneComic(String url){
        return createOrUpdateComic(url);
    }

    @Override
    public String crawlByCategory(String categoryUrlName) {
        String url = "https://truyenfull.vn/the-loai/" + categoryUrlName + "/";
//        List<Comic> comics = new ArrayList<Comic>();
        for (int i = 1; i < 3; i++) {
            System.out.println("Page: " + i);
            try {
                Document document = Jsoup.connect(url + "trang-" + i + "/").get();
                Elements elements = document.select(
                        "#list-page > div.col-xs-12.col-sm-12.col-md-9.col-truyen-main > div.list.list-truyen.col-xs-12 > div.row");
                for (Element element : elements) {
                    String comicUrl = element.select("h3.truyen-title > a").attr("href");
                    if (createOrUpdateComic(comicUrl).equals("that bai")) {
                        return "crawl truyen co url " + comicUrl + " that bai";
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
        }
        return "crawl truyen thanh cong";
    }

    private String createOrUpdateComic(String comicUrl) {
        try {
            Comic comic;

            String urlName = getLastPathUrl(comicUrl);

            comic = comicRepository.findByUrlName(urlName);

            if (comic != null) {
                // truyen da insert
                comic = updateComic(comicUrl, comic);
            } else {
                // truyen chua insert
                comic = createNewComic(comicUrl);
            }
            comicRepository.save(comic);
            return "thanh cong";
        } catch (Exception e) {
            e.printStackTrace();
            return "thai bai";
        }
    }

    private String getLastPathUrl(String url) {
        String[] urlPath = url.split("/");
        return urlPath[urlPath.length - 1];
    }

    private Comic updateComic(String comicUrl, Comic comic) {
        TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
        System.out.println("update " + comic.getTitle());
        //update comic information
        comic = getComicInfo(comicUrl, comic);

        try {
            assert comic != null;
            int numberChapters = comic.getChapters().size();
            System.out.println("numberChapter " + numberChapters);

            int paginationCheck = (int) Math.ceil(numberChapters / 50.0);
            System.out.println("paginationCheck " + paginationCheck);

            int numberChaptersInsertedInPaginationCheck = numberChapters % 50;
            System.out.println("numberChaptersInsertedInPaginationCheck " + numberChaptersInsertedInPaginationCheck);

            comicUrl = comicUrl + "trang-" + paginationCheck + "/#list-chapter";
            Document document = Jsoup.connect(comicUrl).get();
            int totalPage = Integer.parseInt(document.select(comicSelector.totalPage()).val());

            Elements chapterLinks = document.select(comicSelector.chapterLinks());
            int numberChaptersInPaginationCheck;
            try {
                numberChaptersInPaginationCheck = chapterLinks.size();
            } catch (Exception e) {
                numberChaptersInPaginationCheck = 0;
            }
            System.out.println("numberChaptersInPaginationCheck " + numberChaptersInPaginationCheck);

            if (numberChaptersInPaginationCheck > numberChaptersInsertedInPaginationCheck) {
                System.out.println("truyen co chapter moi");
                do {
                    chapterLinks = document.select(comicSelector.chapterLinks());
                    for (int i = numberChaptersInsertedInPaginationCheck; i < numberChaptersInPaginationCheck; i++) {
                        Chapter chapter = getChapterData(chapterLinks.get(i).attr("href"));
                        System.out.println(chapter.getTitle());
                        comic.addChapter(chapter);
                    }
                    paginationCheck++;
                    document = Jsoup.connect(comicUrl + "trang-" + paginationCheck + "/#list-chapter").get();
                } while (paginationCheck <= totalPage);
            } else {
                System.out.println("truyen khong co chapter moi");
            }
        } catch (Exception e) {
            e.printStackTrace();
            comic = null;
        }
        return comic;
    }

    private Comic createNewComic(String comicUrl) {
        System.out.println("create new comic");

        Comic comic = getComicInfo(comicUrl, new Comic());


        if (comic != null) {
            comic.setUrlName(getLastPathUrl(comicUrl));
            comic.setViews(0L);

            //add Chapters to this Comic
            try {
                List<String> allChapterLinks = getAllListChapterLinks(comicUrl);
                assert allChapterLinks != null;
                for (String chapterUrl : allChapterLinks) {
                    Chapter chapter = getChapterData(chapterUrl);
                    System.out.println(chapter.getTitle());
                    comic.addChapter(chapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
                comic = null;
            }
        }
        return comic;
    }

    private Comic getComicInfo(String comicUrl, Comic comic) {
        TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
        try {
            Document document = Jsoup.connect(comicUrl).get();

            comic.setTitle(document.selectFirst(comicSelector.title()).text());
            System.out.println(comic.getTitle());

            comic.setDescription(document.selectFirst(comicSelector.description()).text());
            System.out.println(comic.getDescription());

            Elements authorLinks = document.select(comicSelector.author());
            List<Author> authors = new ArrayList<>();
            for (Element link : authorLinks) {
                Author author = authorRepository.findByFullName(link.text());
                if (author == null) {
                    author = authorRepository.save(new Author(link.text()));
                }
                authors.add(author);
            }
            comic.setAuthors(authors);
            System.out.println(comic.getAuthors().toString());

            Elements categoryLinks = document.select(comicSelector.categoryLinks());
            List<Category> categories = new ArrayList<>();
            for (Element link : categoryLinks) {
                Category category = categoryRepository.findByName(link.text());
                categories.add(category);
            }
            comic.setCategories(categories);
            System.out.println(comic.getCategories().toString());

            if (document.selectFirst(comicSelector.status_full()) == null) {
                comic.setStatus("Đang ra");
            } else {
                comic.setStatus("Full");
            }
            System.out.println(comic.getStatus());

            comic.setRating(Double.parseDouble(document.selectFirst(comicSelector.rating()).text()));
            System.out.println(comic.getRating());

            return comic;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Chapter getChapterData(String chapterUrl) {
        TruyenFullChapterSelector chapterSelector = TruyenFullChapterSelector.getInstance();
        Chapter chapter = new Chapter();
        try {
            Document document = Jsoup.connect(chapterUrl).get();

            chapter.setTitle(document.selectFirst(chapterSelector.title()).text());
            chapter.setContent(document.selectFirst(chapterSelector.content()).text());
            chapter.setUrlName(getLastPathUrl(chapterUrl));
        } catch (IOException e) {
            e.printStackTrace();
            chapter = null;
        }
        return chapter;
    }

    private List<String> getAllListChapterLinks(String comicUrl) {
        TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
        List<String> allChapterLinks = new ArrayList<>();

        try {
            List<String> paginationLinks = getAllPaginationLinks(comicUrl);
            assert paginationLinks != null;
            for (String paginationLink : paginationLinks) {
                Document document = Jsoup.connect(paginationLink).get();
                Elements chapterLinks = document.select(comicSelector.chapterLinks());
                for (Element link : chapterLinks) {
                    allChapterLinks.add(link.attr("href"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return allChapterLinks;
    }

    private List<String> getAllPaginationLinks(String comicUrl) {
        TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
        try {
            Document document = Jsoup.connect(comicUrl).get();

            List<String> list = new ArrayList<>();
            String paginationLink;
            int totalPage = Integer.parseInt(document.select(comicSelector.totalPage()).val());
            for (int i = 0; i < totalPage; i++) {
                paginationLink = comicUrl + "trang-" + (i + 1) + "/#list-chapter";
                list.add(paginationLink);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
