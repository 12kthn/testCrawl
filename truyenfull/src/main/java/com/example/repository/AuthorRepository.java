package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{

	public Author findByFullName(String fullName);
	
}
