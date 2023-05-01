package com.palla.gallery.service;

import com.palla.gallery.dto.ImageDto;
import com.palla.gallery.dto.ImageResponseDto;
import com.palla.gallery.dto.KafkaEventDto;
import com.palla.gallery.entity.Image;
import com.palla.gallery.entity.User;
import com.palla.gallery.exception.ImageNotFoundException;
import com.palla.gallery.exception.UserNotFoundException;
import com.palla.gallery.messaging.KafkaProducer;
import com.palla.gallery.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ImageService {

    private final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    private ImageRepository repository;

    @Autowired
    private ImgurService imgurService;

    @Autowired
    private UserService userService;

    @Autowired
    private KafkaProducer kafkaProducer;

    public ImageDto getImageById(int imageId) {
        Optional<Image> response = repository.findById(imageId);
        if (response.isPresent())
            return new ImageDto(response.get().getId(), response.get().getLink());
        LOGGER.error("ImageService: getImageById() --> Image not found with id " + imageId);
        throw new ImageNotFoundException("Image not found");
    }

    public String deleteImageById(int imageId) throws IOException {
        Optional<Image> response = repository.findById(imageId);
        if (response.isPresent()) {
            repository.deleteById(imageId);
            LOGGER.info("ImageService: deleteImageById() --> Image deleted from db");
            imgurService.deleteImage(response.get().getDeleteHash());
            LOGGER.info("ImageService: deleteImageById() --> Image deleted from Imgur");
            return "Image deleted successfully";
        }

        LOGGER.info("ImageService: deleteImageById() --> Image not found with id " + imageId);
        throw new ImageNotFoundException("No Image found with the id " + imageId);
    }

    public List<ImageDto> getAllImagesByUser(String user) {
        User userResponse = userService.getUserByUsername(user);
        return repository.findAllByUser(userResponse).stream().map(img -> new ImageDto(img.getId(), img.getLink())).toList();
    }

    public List<ImageDto> saveImages(List<MultipartFile> files, String user) throws UserNotFoundException {
        User userResponse = userService.getUserByUsername(user);
        LOGGER.info("ImageService: saveImages() --> Bulk Images upload processing started");
        List<Image> uploadedImages = files.parallelStream().map(file -> {
            try {
                ImageResponseDto imgResponse = imgurService.uploadImage(file);
                kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image uploaded successfully", "Success"));
                return buildImage(imgResponse, userResponse);
            } catch (IOException e) {
                LOGGER.error("ImageService: saveImages() --> Error occurred while uploading image, Image Name: " + file.getOriginalFilename() + ", Message: " + e.getMessage());
                kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image upload failed", "Failed"));
                return buildImage(null, userResponse);
            }
        }).filter(file -> file.getLink() != null).toList();
        LOGGER.info("ImageService: saveImages() --> Bulk Images upload processing completed");
        LOGGER.info("ImageService: saveImages() --> Total " + uploadedImages.size() + " uploaded successfully");
        return repository.saveAll(uploadedImages).stream().map(img -> new ImageDto(img.getId(), img.getLink())).toList();
    }

    public ImageDto saveImage(MultipartFile file, String user) throws UserNotFoundException {
        User userResponse = userService.getUserByUsername(user);
        LOGGER.info("ImageService: saveImage() --> Single Image upload processing started");
        Image uploadedImage = null;
        try {
            uploadedImage = buildImage(imgurService.uploadImage(file), userResponse);
            kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image uploaded successfully", "Success"));
        } catch (IOException e) {
            LOGGER.error("ImageService: saveImage() --> Error occurred while uploading image, Image Name: " + file.getOriginalFilename() + ", Message: " + e.getMessage());
            kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image upload failed", "Failed"));
            uploadedImage = buildImage(null, userResponse);
        }
        Image imgResponse = repository.save(uploadedImage);
        LOGGER.info("ImageService: saveImage() --> Single Image upload processing completed");
        return new ImageDto(imgResponse.getId(), imgResponse.getLink());
    }

    private Image buildImage(ImageResponseDto uploadResponse, User user) {
        Image img = new Image();
        if (uploadResponse != null) {
            img.setLink(uploadResponse.getData().getLink());
            img.setHash(uploadResponse.getData().getId());
            img.setDeleteHash(uploadResponse.getData().getDeletehash());
        }
        img.setUser(user);
        return img;
    }

}
