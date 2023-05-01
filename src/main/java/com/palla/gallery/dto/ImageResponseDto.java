package com.palla.gallery.dto;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDto {

    private ImageResponseDataDto data;
    private boolean success;
    private long status;

}
