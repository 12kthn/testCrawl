package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Author;
import com.example.model.Category;
import com.example.model.Chapter;
import com.example.model.Comic;
import com.example.repository.AuthorRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.ComicRepository;
import com.example.selector.impl.TruyenFullCategorySelector;
import com.example.selector.impl.TruyenFullChapterSelector;
import com.example.selector.impl.TruyenFullStorySelector;
import com.example.utils.ResponseUtil;

@RestController
@RequestMapping("/api/crawler")
public class CrawlController {

	@Autowired
	ComicRepository comicRepository;

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	CategoryRepository categoryRepository;
	

	//insert truyenfull category
	@PostMapping(value = "/insertAllCategories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String crawlerInsertALL() {
		TruyenFullCategorySelector categorySelector = TruyenFullCategorySelector.getInstance();
		List<Category> categories = new ArrayList<>();
		try {
			Document document = Jsoup.connect("https://truyenfull.vn/").get();
			Elements categoryLinks = document.select(categorySelector.name());
			for (Element element : categoryLinks) {
				Category category = new Category(element.text());
				categories.add(category);
			}
		} catch (Exception e) {
			return "";
		}
		categoryRepository.saveAll(categories);
		return ResponseUtil.returnListCategory(categoryRepository.findAll()).toString();
	}

	// Insert comic
	@PostMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String insertData(@RequestParam("url") String url) {
		Comic comic = new Comic();
		try {
			Document document = Jsoup.connect(url).get();
			comic = getComic(comic, document, url);
			comicRepository.save(comic).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return ResponseUtil.returnComic(comic).toString();
	}

	@GetMapping
    public boolean crawlCommic() throws IOException {
        for (int i = 1; i < 5; i++) {
            System.out.println("Page: " + i);
            Document document = Jsoup.connect("https://truyenfull.vn/danh-sach/truyen-hot/trang-" + i + "/").get();
            Elements elements = document.select("#list-page > div.col-xs-12.col-sm-12.col-md-9.col-truyen-main > div.list.list-truyen.col-xs-12 > div.row");
            for (Element element : elements) {
                System.out.println("Tên truyện: " + element.select("h3.truyen-title > a").text());
                System.out.println(element.select("h3.truyen-title > a").attr("href"));
            }
        }

        return true;
    }
	
	// check comic data
	@GetMapping(value = "/comic", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public String getData(@RequestParam("url") String url) {
		Comic comic = new Comic();
		try {
			Document document = Jsoup.connect(url).get();
			comic = getComic(comic, document, url);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return ResponseUtil.returnComic(comic).toString();
	}

	
	
	private Comic getComic(Comic comic, Document document, String comicUrl) {
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		
		List<String> allChapterLinks = getAllListChapterLinks(document, comicUrl);

		comic.setTitle(document.selectFirst(comicSelector.title()).text());
		comic.setDescription(document.selectFirst(comicSelector.description()).text());

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

		Elements categorieLinks = document.select(comicSelector.categorieLinks());
		List<Category> categories = new ArrayList<>();
		for (Element link : categorieLinks) {
			Category category = categoryRepository.findByName(link.text());
			categories.add(category);
		}
		comic.setCategories(categories);

		comic.setStatus(document.selectFirst(comicSelector.status()).text());

		for (String chapterUrl : allChapterLinks) {
			comic.addChapter(getChapterData(document, chapterUrl));
		}

		return comic;
	}
	
	private Chapter getChapterData(Document document, String chapterUrl) {
		TruyenFullChapterSelector chapterSelector = TruyenFullChapterSelector.getInstance();
		Chapter chapter = new Chapter();
		try {
			document = Jsoup.connect(chapterUrl).get();
			chapter.setTitle(document.selectFirst(chapterSelector.title()).text());
			chapter.setContent(document.selectFirst(chapterSelector.content()).text());
		} catch (IOException e) {
			e.printStackTrace();
			chapter = null;
		}
		return chapter;
	}

	private List<String> getAllListChapterLinks(Document document, String comicUrl) {
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		List<String> paginationlinks = getAllPaginationLinks(document, comicUrl);
		List<String> allChapterLinks = new ArrayList<>();
		try {
			for (String paginationlink : paginationlinks) {
				document = Jsoup.connect(paginationlink).get();
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

	private List<String> getAllPaginationLinks(Document document, String comicUrl) {
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		List<String> list = new ArrayList<>();
		String paginationLink;
		int totalPage = Integer.valueOf(document.select(comicSelector.totalPage()).val());
		for (int i = 0; i < totalPage; i++) {
			paginationLink = comicUrl + "trang-" + (i + 1) + "/#list-chapter";
			list.add(paginationLink);
		}
		return list;
	}

}
