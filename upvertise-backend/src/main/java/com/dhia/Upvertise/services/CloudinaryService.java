package com.dhia.Upvertise.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }


    public Map<String, Object> generateSignedUploadParams() {
        try {
            long timestamp = System.currentTimeMillis() / 1000; // Current timestamp

            // Generate the signature
            Map<String, Object> params = ObjectUtils.asMap(
                    "timestamp", timestamp,
                    "folder", "Upvertice", // Change the folder name if needed
                    "upload_preset", "your_unsigned_preset" // Make sure you have this preset in Cloudinary
            );

            // Compute the signature
            String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);

            // Return the necessary parameters for frontend upload
            Map<String, Object> response = new HashMap<>();
            response.put("cloud_name", cloudinary.config.cloudName);
            response.put("api_key", cloudinary.config.apiKey);
            response.put("timestamp", timestamp);
            response.put("signature", signature);
            response.put("folder", "Upvertice");
            response.put("upload_preset", "your_unsigned_preset");

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error generating signed upload parameters", e);
        }
    }
    // Method to upload profile photo to Cloudinary
    public String uploadProfilePhoto(MultipartFile file) throws IOException {
        // Check if the file is not empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        // Validate file type (optional)
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Upload file to Cloudinary's 'profile_photos' folder
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "profile_photos"));

        // Return the secure URL of the uploaded photo
        return uploadResult.get("secure_url").toString();
    }

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "Upvertice"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }
    }
    public void deleteImage(String imageUrl) {
        try {
            // Extract public ID from Cloudinary URL
            String publicId = extractPublicId(imageUrl);

            if (publicId != null) {
                // Call Cloudinary API to delete the image
                Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

                // Log or handle response if needed
                System.out.println("Delete response: " + result);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
    }

    private String extractPublicId(String imageUrl) {
        try {
            // Cloudinary URLs typically have this structure: https://res.cloudinary.com/{cloud_name}/image/upload/{public_id}.{format}
            String[] parts = imageUrl.split("/");
            String fileNameWithExtension = parts[parts.length - 1]; // e.g., "abc123.png"
            return fileNameWithExtension.split("\\.")[0]; // Extracts "abc123"
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract public ID from image URL", e);
        }
    }

    public void deleteImagesFromCloudinary(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        // Extract public ID from URL (Cloudinary format: https://res.cloudinary.com/{cloud_name}/image/upload/v123456789/{public_id}.jpg)
                        String publicId = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
                        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete image from Cloudinary: " + imageUrl, e);
                    }
                }
            }
        }

    }
    public String getImageUrl(String imageName) {
        try {
            // Generate the URL of the image by passing the image name (public ID)
            return cloudinary.url().generate(imageName);
        } catch (Exception e) {
            // Handle exception (e.g., log it)
            return null;
        }
    }
}
