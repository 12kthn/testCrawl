package com.truyenfull.query.repository;

import com.truyenfull.query.model.Chapter;
import com.truyenfull.query.model.Comic;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long>{

    @Query(value = "select c from Chapter c where c.id in (select max(c.id) from c where c.comic = ?1)", nativeQuery = false)
    Chapter findLastChapterOfComic(Comic comic);

    List<Chapter> findByComic(Comic comic, Pageable pageable);
}
