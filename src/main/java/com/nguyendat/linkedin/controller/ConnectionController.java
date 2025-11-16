package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.entity.Connection;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.ConnectionRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/connections")
public class ConnectionController {
    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    public ConnectionController(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(connectionRepository.findAll());
    }

    @GetMapping("/pending")
    public ResponseEntity<?> pending() {
        // placeholder
        return ResponseEntity.ok(connectionRepository.findAll());
    }

    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestParam Long receiverId) {
        Optional<User> r = userRepository.findById(receiverId);
        Optional<User> requester = userRepository.findAll().stream().findFirst();
        if (r.isEmpty() || requester.isEmpty()) return ResponseEntity.badRequest().build();
        Connection c = new Connection();
        c.setRequester(requester.get());
        c.setReceiver(r.get());
        c.setCreatedAt(OffsetDateTime.now());
        c.setStatus("pending");
        connectionRepository.save(c);
        return ResponseEntity.status(201).body(c);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        Optional<Connection> cOpt = connectionRepository.findById(id);
        if (cOpt.isEmpty()) return ResponseEntity.notFound().build();
        Connection c = cOpt.get();
        c.setStatus("accepted");
        c.setAcceptedAt(OffsetDateTime.now());
        connectionRepository.save(c);
        return ResponseEntity.ok(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        connectionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
