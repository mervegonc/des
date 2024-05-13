package com.tobias.des.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_photos")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostPhotos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "photo_name")
	private String photoName;

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	// Gerekli constructor, getter ve setter metotları buraya eklenecek
}
