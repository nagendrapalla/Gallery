package com.palla.gallery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "GALLERY_USER")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GAL_USER_ID", unique = true)
    private int userId;

    @Column(name = "GAL_USER_NAME", unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    @Column(name = "GAL_PASSWORD")
    @NotBlank(message = "Password is mandatory")
    private String password;

}
