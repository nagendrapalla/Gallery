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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
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

    /*
    * Fetching Image Details by {{imageID}}
    * */
    public ImageDto getImageById(int imageId) {
        Optional<Image> response = repository.findById(imageId);
        if (response.isPresent())
            return new ImageDto(response.get().getId(), response.get().getLink());
        LOGGER.error("ImageService: getImageById() --> Image not found with id " + imageId);
        throw new ImageNotFoundException("Image not found");
    }


    /*
     * Deleting Image by {{imageID}}
     * 1. Delete Record form DB
     * 2. Delete Image from Imgur
     * */
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

    /*
     * Fetch all images associated to user by {{ userId }}
     * */
    public List<ImageDto> getAllImagesByUser(String user) {
        User userResponse = userService.getUserByUsername(user);
        return repository.findAllByUser(userResponse).stream().map(img -> new ImageDto(img.getId(), img.getLink())).toList();
    }


    /*
     * Bulk Image Upload Processing
     * 1. Upload images to Imgur by using parallel stream method
     * 2. Creating new record in DB for each successfully uploaded images
     * */
    public List<ImageDto> saveImages(List<MultipartFile> files, String user) throws UserNotFoundException {
        LOGGER.info("ImageService: saveImages() --> Bulk Images upload processing started");
        List<Image> uploadedImages = files.stream().map(file -> {
            try {
                return uploadImage(file, user);
            } catch (IOException e) {
                return null;
            }
        }).filter(Objects::nonNull).toList();
        LOGGER.info("ImageService: saveImages() --> Bulk Images upload processing completed");
        LOGGER.info("ImageService: saveImages() --> Total " + uploadedImages.size() + " uploaded successfully");
        return repository.saveAll(uploadedImages).stream().map(img -> new ImageDto(img.getId(), img.getLink())).toList();
    }


    /*
     * Single Image Upload Processing
     * 1. Upload image to Imgur
     * 2. Creating new record in DB uploaded image
     * */
    public ImageDto saveImage(MultipartFile file, String user) throws UserNotFoundException, IOException {
        LOGGER.info("ImageService: saveImage() --> Single Image upload processing started");
        Image imgResponse = uploadImage(file, user);
        LOGGER.info("ImageService: saveImage() --> Single Image upload processing completed");
        return new ImageDto(imgResponse.getId(), imgResponse.getLink());
    }

    private Image uploadImage(MultipartFile file, String user) throws IOException {
        User userResponse = userService.getUserByUsername(user);
        try {
            // Image Uploading to Imgur API
            ImageResponseDto imgResponse = imgurService.uploadImage(file);

            // Sending message to messing queue via Kafka Producer
            kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image uploaded successfully", "Success"));

            // Building ImageDTO with required information to store in DB
            return buildImage(imgResponse, userResponse);
        } catch (IOException e) {
            LOGGER.error("ImageService: saveImages() --> Error occurred while uploading image, Image Name: " + file.getOriginalFilename() + ", Message: " + e.getMessage());
            kafkaProducer.writeMessage(new KafkaEventDto(userResponse.getUsername(), file.getOriginalFilename(), "Image upload failed", "Failed"));
        }

        throw new IOException("Image Upload Failed");
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

//    @KafkaListener(topics = "imgur_api_topic", groupId = "group-id")
//    public void consume(String message) {
//        System.out.printf("User created -> %s", message);
//    }

}
