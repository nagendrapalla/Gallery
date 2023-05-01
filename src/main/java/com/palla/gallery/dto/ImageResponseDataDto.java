package com.palla.gallery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigInteger;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponseDataDto {

    private String id;
    private String title;
    private String description;
    private BigInteger datetime;
    private String type;
    private String name;
    private String link;
    private String deletehash;

}
