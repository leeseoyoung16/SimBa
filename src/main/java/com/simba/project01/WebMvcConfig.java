package com.simba.project01;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.mission-upload-dir}")
    private String missionDir;

    @Value("${file.review-upload-dir}")
    private String reviewDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String missionPath = java.nio.file.Paths.get(missionDir.replace("\\", "/")).toUri().toString();
        String reviewPath  = java.nio.file.Paths.get(reviewDir.replace("\\", "/")).toUri().toString();

        registry.addResourceHandler("/uploads/mission/**")
                .addResourceLocations(missionPath);

        registry.addResourceHandler("/uploads/review/**")
                .addResourceLocations(reviewPath);
    }
}



