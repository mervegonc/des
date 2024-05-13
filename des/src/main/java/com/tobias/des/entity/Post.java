package com.tobias.des.entity;

import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
import lombok.Data;

@Entity
@Table(name = "posts")
@Data

public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	User user;

	String title;

	/*
	 * @Lob
	 * 
	 * @Column(columnDefinition = "text") String text;
	 */
	@Column(length = 380)
	String text;

	// Post entity'sindeki ilişkilendirme
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostPhotos> photos;

}
