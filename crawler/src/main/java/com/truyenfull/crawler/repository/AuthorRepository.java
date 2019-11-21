package com.truyenfull.crawler.repository;

import com.truyenfull.crawler.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{

	Author findByFullName(String fullName);
	
}
