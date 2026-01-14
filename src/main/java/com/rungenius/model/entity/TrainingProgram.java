package com.rungenius.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgram {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 50)
    private String raceType;
    
    @Column(nullable = false)
    private double distanceKm;
    
    @Column(nullable = false)
    private String niveau;
    
    @Column(nullable = false)
    private int sorties;
    
    @Column(nullable = false)
    private double vma;
    
    @Column(length = 20)
    private String objectif;
    
    @Column
    private LocalDate raceDate;
    
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String programData;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
