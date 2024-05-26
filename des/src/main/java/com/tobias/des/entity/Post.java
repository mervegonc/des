package com.tobias.des.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
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

//Diğer importlar ve kodlar

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@CreationTimestamp
	private Date createdAt;

	// Post entity'sindeki ilişkilendirme
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	private List<PostPhotos> photos;

	private String formattedCreatedAt;

	public String getFormattedCreatedAt() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return dateFormat.format(this.createdAt);
	}

	public void setCreatedAtFormatted(String formattedCreatedAt) {
		// Oluşturulan formatlanmış oluşturma zamanını ayarla
		this.formattedCreatedAt = formattedCreatedAt;
	}

}
