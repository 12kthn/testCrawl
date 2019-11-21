package com.truyenfull.query.repository;

import com.truyenfull.query.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{

	Category findByUrlName(String urlName);
	
}
