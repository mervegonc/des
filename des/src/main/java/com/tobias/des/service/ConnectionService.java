package com.tobias.des.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tobias.des.entity.Connection;
import com.tobias.des.entity.User;
import com.tobias.des.repository.ConnectionRepository;
import com.tobias.des.repository.UserRepository;

@Service
@Transactional
public class ConnectionService {

	private final ConnectionRepository connectionRepository;
	private final UserRepository userRepository;

	public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
		this.connectionRepository = connectionRepository;
		this.userRepository = userRepository;
	}

	public List<Connection> getAllConnections() {
		return connectionRepository.findAll();
	}

	public Connection getConnectionById(Long id) {
		return connectionRepository.findById(id).orElse(null);
	}

	public List<Connection> getConnectionsByUserId(Long userId) {
		return connectionRepository.findByUserId(userId);
	}

	public Connection saveConnection(Connection connection) {
		// User nesnesini repository'den yükle
		User user = userRepository.findById(connection.getUser().getId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		connection.setUser(user);
		return connectionRepository.save(connection);
	}

	public void deleteConnection(Long id) {
		Connection connection = connectionRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Connection not found"));
		connectionRepository.delete(connection);
	}

}
