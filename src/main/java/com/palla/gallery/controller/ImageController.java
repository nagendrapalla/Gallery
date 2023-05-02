package com.palla.gallery.controller;

import com.palla.gallery.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/upload/multiple", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadImage(@Valid @RequestPart List<MultipartFile> files, @RequestPart String username) {
        return new ResponseEntity<>(imageService.saveImages(files, username), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile file, @RequestPart String username) throws IOException {
        return new ResponseEntity<>(imageService.saveImage(file, username), HttpStatus.ACCEPTED);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getAllImagesByUser(@PathVariable String username) {
        return new ResponseEntity<>(imageService.getAllImagesByUser(username), HttpStatus.OK);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<?> getImageById(@PathVariable int imageId) {
        return new ResponseEntity<>(imageService.getImageById(imageId), HttpStatus.OK);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImageById(@PathVariable int imageId) throws IOException {
        return new ResponseEntity<>(imageService.deleteImageById(imageId), HttpStatus.OK);
    }

}
