package com.tql.backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.tql.backend.dto.ImageRequest;
import com.tql.backend.model.Image;
import com.tql.backend.model.User;
import com.tql.backend.repository.ImageRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    private ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostMapping("/updateImage")
    public Map<String, Object> updateImage(
            @RequestParam String publicId,
            @RequestBody Map<String, Object> transformations
    ) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));

        // Cập nhật ảnh với transformations
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                "https://res.cloudinary.com/" + cloudName + "/image/upload/" + publicId,
                ObjectUtils.asMap(
                        "transformation", transformations,
                        "public_id", publicId,
                        "invalidate", true
                ));

        return uploadResult;
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadImage(
            @RequestParam String publicId,
            @RequestParam(required = false) String[] transformations) {

        System.out.println("Generating URL for publicId: " + publicId);
        System.out.println("Transformations: " + Arrays.toString(transformations));

        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));

        try {
            System.out.println("Transformations received: " + Arrays.toString(transformations));
            // Tạo transformation
            Transformation transformation = new Transformation();
            if (transformations != null) {
                for (String t : transformations) {
                    // Sử dụng SDK để parse và thêm transformation
                    if (t.startsWith("w_")) {
                        transformation.width(Integer.parseInt(t.substring(2)));
                    } else if (t.startsWith("h_")) {
                        transformation.height(Integer.parseInt(t.substring(2)));
                    } else if (t.startsWith("c_")) {
                        transformation.crop(t.substring(2));
                    } else if (t.startsWith("e_")) {
                        transformation.effect(t.substring(2));
                    } else if (t.startsWith("b_")) {
                        if (t.contains("blur")) {
                            transformation.effect("blur:" + t.substring(t.indexOf(":") + 1));
                        } // ... xử lý các effect bắt đầu bằng "b_" khác
                    } else if (t.startsWith("q_")) {
                        transformation.quality(t.substring(2));
                    } // ... xử lý các loại transformation khác
                    else if (t.startsWith("e_")) {
                        if (t.contains("object_removal")) {
                            String objectRemovalParams = t.substring(t.indexOf(":") + 1);
                            transformation.effect("object_removal:" + objectRemovalParams);
                        } else {
                            transformation.effect(t.substring(2));
                        }
                    }
                }
            }

            // Tạo URL ảnh
            String imageUrl = cloudinary.url()
                    .transformation(transformation)
                    .generate(publicId);
            System.out.println("Final Cloudinary URL: " + imageUrl); // <--- LOG QUAN TRỌNG

            // Tải ảnh từ Cloudinary
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                String errorMessage = "Unauthorized access to image: " + publicId;
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage.getBytes());
            }
            else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                String errorMessage = "Cloudinary error: Image not found - " + publicId;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage.getBytes());
            }
            else if (connection.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                String errorContent = IOUtils.toString(connection.getErrorStream(), StandardCharsets.UTF_8);
                String errorMessage = "Cloudinary error [" + connection.getResponseCode() + "]: " + errorContent;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.getBytes());
            }

            // Kiểm tra lỗi từ Cloudinary
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(("Cloudinary error: " + connection.getResponseMessage()).getBytes());
            }

            // Đọc dữ liệu ảnh
            InputStream inputStream = connection.getInputStream();
            byte[] imageBytes = IOUtils.toByteArray(inputStream); // Sử dụng Apache Commons IO
            inputStream.close();

            // Thiết lập headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(publicId + ".jpg")
                    .build());


            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(("Error processing image: " + e.getMessage()).getBytes());
        }

    }

    @PostMapping
    public ResponseEntity<Image> saveImageInfo(@RequestBody ImageRequest request) {
        Image image = new Image();
        image.setFileName(request.getPublicId());
        User user = new User();
        user.setId(request.getUserId());
        image.setUser(user);
        imageRepository.save(image);
        return ResponseEntity.ok(image);
    }

    @GetMapping
    public ResponseEntity<java.util.List<Image>> listImages(@RequestParam Long userId) {
        List<Image> images = imageRepository.findByUserId(userId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/all-images")
    public ResponseEntity<String> deleteAllImages() {
        imageRepository.deleteAll();
        return ResponseEntity.ok("All images deleted successfully");
    }
}