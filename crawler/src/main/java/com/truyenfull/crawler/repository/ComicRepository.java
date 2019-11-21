package com.truyenfull.crawler.repository;

import com.truyenfull.crawler.model.Comic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicRepository extends JpaRepository<Comic, Long>{

	Comic findByUrlName(String urlName);
}
