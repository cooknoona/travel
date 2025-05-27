package com.travel.dto.request;

import com.travel.entity.Guide;
import com.travel.entity.Tour;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Record request dto for a guide to post a tour */
public record TourPostRequest(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "content is required")
        String content
) {
    public Tour toEntity(Guide guide) {
        return Tour.builder()
                .title(title)
                .content(content)
                .guide(guide)
                .build();
    }
}
