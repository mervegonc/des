package com.tobias.des.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//Diğer importlar ve kodlar

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Null olan alanları JSON'a dahil etmez
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // Hibernate proxy nesnelerini göz ardı et
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.EAGER) // EAGER olarak değiştirildi
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	User user;

	String title;

	@Column(length = 380)
	String text;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@CreationTimestamp
	private Date createdAt;

	@ManyToOne(fetch = FetchType.EAGER) // EAGER olarak değiştirildi
	@JoinColumn(name = "article_id")
	private Article article;

	@Transient
	private String formattedCreatedAt;

	@Column(length = 200)
	String connections;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostPhotos> photos;

	@JsonProperty("articleId")
	public Long getArticleId() {
		return article != null ? article.getId() : null;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getFormattedCreatedAt() {
		if (createdAt != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
			return sdf.format(createdAt);
		}
		return null;
	}

	public void setFormattedCreatedAt(String formattedCreatedAt) {
		this.formattedCreatedAt = formattedCreatedAt;
	}

	public void setCreatedAtFormatted(String formattedCreatedAt2) {
		// TODO Auto-generated method stub

	}
}