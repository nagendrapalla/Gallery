package com.palla.gallery.repository;

import com.palla.gallery.entity.Image;
import com.palla.gallery.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findAllByUser(User user);
}
