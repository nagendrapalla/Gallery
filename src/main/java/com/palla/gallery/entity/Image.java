package com.palla.gallery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GALLERY_IMAGES")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private User user;

    private String link;

    private String hash;

    private String deleteHash;

}
