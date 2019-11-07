package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Comic;
@Repository
public interface ComicRepository extends JpaRepository<Comic, Long>{

	Comic findByTitle(String title);
	
}
