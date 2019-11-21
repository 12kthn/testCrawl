package com.truyenfull.query.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comics")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createAt", "updateAt"}, allowGetters = true, allowSetters = true)
public class Comic implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	@Column(columnDefinition = "text")
	private String description;

	@Column
	private String urlName;

	@Column
	private double rating;

	@Column
	private Long views;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "comics_categories",
			joinColumns = {@JoinColumn(name = "comic_id")},
			inverseJoinColumns = { @JoinColumn(name = "category_id") })
	private List<Category> categories;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "comics_authors",
			joinColumns = {@JoinColumn(name = "comic_id")},
			inverseJoinColumns = { @JoinColumn(name = "author_id") })
	private List<Author> authors;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "comic", orphanRemoval = true)
	private List<Chapter> chapters = new ArrayList<>();
	
	private String status;
	
	@Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createAt;
    
    @Column(name = "update_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updateAt;
    
    public void addChapter(Chapter chapter) {
		chapters.add(chapter);
		chapter.setComic(this);
	}
}
