package com.palla.gallery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KafkaEventDto {

    private String username;
    private String imageName;
    private String message;
    private String status;

}
