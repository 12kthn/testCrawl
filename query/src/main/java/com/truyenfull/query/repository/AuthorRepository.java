package com.truyenfull.query.repository;

import com.truyenfull.query.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{

	Author findByFullName(String fullName);
	
}
