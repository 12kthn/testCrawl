package com.example.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.example.services.CrawlerService;
import com.example.utils.ResponseUtil;

@Service
public class CrawlerServiceImpl implements CrawlerService {

	@Autowired
	AuthorRepository authorRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ComicRepository comicRepository;

	@Override
	public List<Category> saveListCategories(String url) throws IOException {
		TruyenFullCategorySelector categorySelector = TruyenFullCategorySelector.getInstance();
		List<Category> categories = new ArrayList<>();
		try {
			Document document = Jsoup.connect("https://truyenfull.vn/").get();
			Elements categoryLinks = document.select(categorySelector.name());
			for (Element element : categoryLinks) {
				categories.add(new Category(element.text()));
			}
		} catch (Exception e) {
			return null;
		}
		categories = categoryRepository.saveAll(categories);
		return categories;
	}

	@Override
	public Comic getOneComic(String comicUrl) throws IOException {
		Comic comic = new Comic();
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		Document document = Jsoup.connect(comicUrl).get();

		String title = document.selectFirst(comicSelector.title()).text();

		comic = comicRepository.findByTitle(title);

		if (comic != null) {
			// truyen da insert
			comic = updateComic(comicUrl, comic);
		} else {
			// truyen chua insert
			comic = createNewComic(comicUrl);
		}
		return comic;
	}
	
	@Override
	public Comic saveOneComic(String comicUrl) throws IOException {
		return createOrUpdateComic(comicUrl);
	}

	@Override
	public List<Comic> saveListComics(String url) throws IOException {
		List<Comic> comics = new ArrayList<Comic>();
		for (int i = 1; i < 2; i++) {
			System.out.println("Page: " + i);
			Document document = Jsoup.connect(url + "trang-" + i + "/").get();
			Elements elements = document.select(
					"#list-page > div.col-xs-12.col-sm-12.col-md-9.col-truyen-main > div.list.list-truyen.col-xs-12 > div.row");
			for (Element element : elements) {
				String comicUrl = element.select("h3.truyen-title > a").attr("href");
				Comic comic = createOrUpdateComic(comicUrl);
				comics.add(comic);
			}
		}
		return comics;
	}

	public Comic createOrUpdateComic(String comicUrl) throws IOException {
		Comic comic = new Comic();
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		Document document = Jsoup.connect(comicUrl).get();

		String title = document.selectFirst(comicSelector.title()).text();

		comic = comicRepository.findByTitle(title);

		if (comic != null) {
			// truyen da insert
			comic = updateComic(comicUrl, comic);
		} else {
			// truyen chua insert
			comic = createNewComic(comicUrl);
		}
		return comicRepository.save(comic);
	}

	private Comic updateComic(String comicUrl, Comic comic) {
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		System.out.println("update " + comic.getTitle());

		try {
			int numberChapters = comic.getChapters().size();
			System.out.println("numberChapters " + numberChapters);
			int paginationCheck = (int) Math.ceil(numberChapters / 50.0);
			System.out.println("paginationCheck " + paginationCheck);
			int numberChaptersInsertedInPaginationCheck = numberChapters % 50;
			System.out.println("numberChaptersInsertedInPaginationCheck " + numberChaptersInsertedInPaginationCheck);

			comicUrl = comicUrl + "trang-" + paginationCheck + "/#list-chapter";
			Document document = Jsoup.connect(comicUrl).get();
			int totalPage = Integer.valueOf(document.select(comicSelector.totalPage()).val());

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
						Chapter chapter = getChapter(document, chapterLinks.get(i).attr("href"));
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
		TruyenFullStorySelector comicSelector = TruyenFullStorySelector.getInstance();
		Comic comic = new Comic();
		System.out.println("create new comic");

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
			System.out.println(ResponseUtil.returnListAuthor(authors));

			Elements categorieLinks = document.select(comicSelector.categorieLinks());
			List<Category> categories = new ArrayList<>();
			for (Element link : categorieLinks) {
				Category category = categoryRepository.findByName(link.text());
				categories.add(category);
			}
			comic.setCategories(categories);
			System.out.println(ResponseUtil.returnListCategory(categories));

			if (document.selectFirst(comicSelector.status_full()) == null) {
				comic.setStatus("Đang ra");
			} else {
				comic.setStatus("Full");
			}
			System.out.println(comic.getStatus());

			List<String> allChapterLinks = getAllListChapterLinks(document, comicUrl);
			for (String chapterUrl : allChapterLinks) {
				Chapter chapter = getChapter(document, chapterUrl);
				System.out.println(chapter.getTitle());
				comic.addChapter(chapter);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return comic;
	}

	private Chapter getChapter(Document document, String chapterUrl) {
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
		try {
			document = Jsoup.connect(comicUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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
