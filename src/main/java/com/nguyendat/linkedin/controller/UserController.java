package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.entity.Post;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.PostRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public UserController(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        // placeholder: return first user
        Optional<User> u = userRepository.findAll().stream().findFirst();
        return u.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return userRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@RequestBody User update) {
        // placeholder: update first user
        Optional<User> uOpt = userRepository.findAll().stream().findFirst();
        if (uOpt.isEmpty()) return ResponseEntity.notFound().build();
        User u = uOpt.get();
        u.setFullName(update.getFullName());
        u.setHeadline(update.getHeadline());
        u.setLocation(update.getLocation());
        u.setAbout(update.getAbout());
        userRepository.save(u);
        return ResponseEntity.ok(u);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<?> getUserPosts(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(postRepository.findAll()))
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO: avatar upload, suggestions
}
