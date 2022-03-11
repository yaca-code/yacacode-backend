package com.ph.ponto.repository;

import java.util.List;
import java.util.Optional;

import com.ph.ponto.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAll();

	Optional<User> findById(Long id);

	User findByName(String name);

}