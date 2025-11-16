package com.nguyendat.linkedin.controller;

import com.nguyendat.linkedin.dto.request.CommentRequest;
import com.nguyendat.linkedin.dto.request.PostCreateRequest;
import com.nguyendat.linkedin.entity.Comment;
import com.nguyendat.linkedin.entity.Post;
import com.nguyendat.linkedin.entity.User;
import com.nguyendat.linkedin.repository.CommentRepository;
import com.nguyendat.linkedin.repository.PostRepository;
import com.nguyendat.linkedin.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping
    public ResponseEntity<?> feed(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostCreateRequest req) {
        // placeholder: attach to first user
        Optional<User> uOpt = userRepository.findAll().stream().findFirst();
        if (uOpt.isEmpty()) return ResponseEntity.badRequest().body("no users");
        User u = uOpt.get();
        Post p = new Post();
        p.setUser(u);
        p.setContent(req.content);
        p.setMediaUrls(req.mediaUrls);
        p.setVisibility(req.visibility == null ? "public" : req.visibility);
        postRepository.save(p);
        return ResponseEntity.status(201).body(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return postRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PostCreateRequest req) {
        Optional<Post> pOpt = postRepository.findById(id);
        if (pOpt.isEmpty()) return ResponseEntity.notFound().build();
        Post p = pOpt.get();
        p.setContent(req.content);
        p.setMediaUrls(req.mediaUrls);
        p.setVisibility(req.visibility == null ? p.getVisibility() : req.visibility);
        postRepository.save(p);
        return ResponseEntity.ok(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        postRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable Long id) {
        // TODO: implement Like entity persistence
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> unlike(@PathVariable Long id) {
        // TODO
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> comment(@PathVariable Long id, @RequestBody CommentRequest req) {
        Optional<Post> pOpt = postRepository.findById(id);
        Optional<User> uOpt = userRepository.findAll().stream().findFirst();
        if (pOpt.isEmpty() || uOpt.isEmpty()) return ResponseEntity.badRequest().build();
        Comment c = new Comment();
        c.setPost(pOpt.get());
        c.setUser(uOpt.get());
        c.setContent(req.content);
        if (req.parentCommentId != null) {
            commentRepository.findById(req.parentCommentId).ifPresent(c::setParentComment);
        }
        commentRepository.save(c);
        return ResponseEntity.status(201).body(c);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        // simple implementation
        return ResponseEntity.ok(commentRepository.findAll());
    }
}
