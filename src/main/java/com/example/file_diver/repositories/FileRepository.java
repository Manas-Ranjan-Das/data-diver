package com.example.file_diver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.file_diver.models.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity,Long> {
    
}
