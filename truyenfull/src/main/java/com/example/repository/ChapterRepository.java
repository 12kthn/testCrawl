package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>{

}
