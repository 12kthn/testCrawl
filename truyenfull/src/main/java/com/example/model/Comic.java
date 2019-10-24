package com.example.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.model.Tag;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@Entity
@Table(name = "comics")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createAt", "updateAt"}, allowGetters = true, allowSetters = true)
public class Comic implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	
	private String description;
	
	private Byte status;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "comics_categories",
			joinColumns = {@JoinColumn(name = "comic_id")},
			inverseJoinColumns = { @JoinColumn(name = "category_id") })
	private List<Category> categories;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "comics_tags",
			joinColumns = {@JoinColumn(name = "comic_id")},
			inverseJoinColumns = { @JoinColumn(name = "tag_id") })
	private List<Tag> tags;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "comics_authors",
			joinColumns = {@JoinColumn(name = "comic_id")},
			inverseJoinColumns = { @JoinColumn(name = "author_id") })
	private List<Author> authors;
	
	@OneToMany(mappedBy = "comic")
	private List<Chapter> chapters;
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createAt;
    
    @Column(name = "update_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updateAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Date updateAt) {
		this.updateAt = updateAt;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	
}
