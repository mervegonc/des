package com.tobias.des.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.entity.Connection;
import com.tobias.des.service.ConnectionService;

@RestController
@RequestMapping("/api/user/connections")
public class ConnectionController {

	private final ConnectionService connectionService;

	public ConnectionController(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	@GetMapping
	public List<Connection> getAllConnections() {
		return connectionService.getAllConnections();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Connection> getConnectionById(@PathVariable Long id) {
		Connection connection = connectionService.getConnectionById(id);
		if (connection != null) {
			return ResponseEntity.ok(connection);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Connection>> getConnectionsByUserId(@PathVariable Long userId) {
		List<Connection> connections = connectionService.getConnectionsByUserId(userId);
		return ResponseEntity.ok(connections);
	}

	@PostMapping
	public Connection createConnection(@RequestBody Connection connection) {
		return connectionService.saveConnection(connection);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteConnection(@PathVariable Long id) {
		connectionService.deleteConnection(id);
		return ResponseEntity.noContent().build();
	}

}
