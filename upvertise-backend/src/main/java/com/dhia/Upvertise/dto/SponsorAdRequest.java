package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "Sponsor Ad Request")
public record SponsorAdRequest(
        @NotBlank(message = "Title cannot be blank")
        @Schema(description = "Title of the ad") String title,

        @NotBlank(message = "Content cannot be blank")
        @Schema(description = "Content of the ad") String content,

        @Schema(description = "List of design colors")
        List<@NotBlank(message = "Design colors cannot be blank") String> designColors
) {}
