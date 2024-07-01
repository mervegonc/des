package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobias.des.entity.User;
import com.tobias.des.entity.UserFollower;

public interface UserFollowerRepository extends JpaRepository<UserFollower, Long> {
	List<UserFollower> findByUserId(Long userId);

	List<UserFollower> findByUser(User user);

	List<UserFollower> findByFollower(User follower);

	UserFollower findByUserAndFollower(User user, User follower);

	boolean existsByUserIdAndFollowerId(Long userId, Long followerId);
}
