package com.example.file_diver.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.file_diver.models.FileEntity;
import com.example.file_diver.models.UserEntity;
import com.example.file_diver.repositories.FileRepository;
import com.example.file_diver.repositories.UserRepository;

@Service
public class FileService {
    @Autowired
    FileRepository fileRepository ;

    @Autowired
    UserRepository userRepository;

    public ResponseEntity<?> saveFile (MultipartFile file,Long userId) throws IOException{
        // Retrieve the user (assuming user existence is already checked)
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate file (size, type, etc.)
        validateFile(file);

        // Map MultipartFile to FileEntity
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileData(file.getBytes()); // Ensure fileData is large enough
        fileEntity.setUploadedBy(user);

        // Save and return
        return new ResponseEntity<FileEntity>(fileRepository.save(fileEntity),HttpStatus.ACCEPTED);
    }

    boolean validateFile(MultipartFile file){
        return true;
    }

}
