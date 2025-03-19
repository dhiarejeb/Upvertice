package com.dhia.Upvertise.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dsdkhrv2l",
                "api_key", "148643876331847",
                "api_secret", "6OgL2wlVLNf2sbo3iHYA0x5-wiI",
                "secure", true
        ));
    }
}
