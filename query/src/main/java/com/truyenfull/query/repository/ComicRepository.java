package com.truyenfull.query.repository;

import com.truyenfull.query.model.Category;
import com.truyenfull.query.model.Comic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ComicRepository extends JpaRepository<Comic, Long>{

	List<Comic> findAllByCategories(Category category, Pageable pageable);

	Comic findByUrlName(String urlName);
	
}
