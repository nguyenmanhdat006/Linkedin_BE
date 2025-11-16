package com.nguyendat.linkedin.repository;

import com.nguyendat.linkedin.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    List<Connection> findByRequesterIdOrReceiverId(Long requesterId, Long receiverId);
}
