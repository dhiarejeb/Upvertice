package com.dhia.Upvertise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Schema(name = "SponsorAdMultipartRequest", description = "Multipart form request for sponsor ad")
public class SponsorAdMultipartRequest {

    @Schema(description = "Sponsor Ad JSON string", type = "string", format = "json")
    private String request;

    @Schema(description = "Image file", type = "string", format = "binary")
    private MultipartFile image;
}
