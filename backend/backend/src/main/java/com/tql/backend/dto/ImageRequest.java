package com.tql.backend.dto;

import lombok.Data;

@Data
public class ImageRequest {
    private String publicId;
    private Long userId;
}
