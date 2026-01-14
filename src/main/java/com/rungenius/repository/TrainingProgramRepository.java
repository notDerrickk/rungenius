package com.rungenius.repository;

import com.rungenius.model.entity.TrainingProgram;
import com.rungenius.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {
    List<TrainingProgram> findByUserOrderByCreatedAtDesc(User user);
    Optional<TrainingProgram> findByIdAndUser(Long id, User user);
}
