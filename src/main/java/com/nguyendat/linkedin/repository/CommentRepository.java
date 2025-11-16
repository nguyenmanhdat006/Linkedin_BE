package com.nguyendat.linkedin.repository;

import com.nguyendat.linkedin.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
