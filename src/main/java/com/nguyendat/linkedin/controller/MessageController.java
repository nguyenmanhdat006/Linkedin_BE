package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.entity.Conversation;
import com.nguyendat.linkedin.entity.Message;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.MessageRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MessageController {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> conversations() {
        // placeholder: return empty
        return ResponseEntity.ok().build();
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(messageRepository.findByConversationIdOrderByCreatedAtAsc(id));
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(@RequestParam Long participantId) {
        // placeholder: not implemented
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<?> sendMessage(@PathVariable Long id, @RequestBody Message m) {
        Optional<User> uOpt = userRepository.findAll().stream().findFirst();
        if (uOpt.isEmpty()) return ResponseEntity.badRequest().build();
        m.setSender(uOpt.get());
        // assume conversation exists
        messageRepository.save(m);
        return ResponseEntity.status(201).body(m);
    }

    @PutMapping("/messages/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        Optional<Message> mOpt = messageRepository.findById(id);
        if (mOpt.isEmpty()) return ResponseEntity.notFound().build();
        Message m = mOpt.get();
        m.setReadAt(java.time.OffsetDateTime.now());
        messageRepository.save(m);
        return ResponseEntity.ok(m);
    }
}
