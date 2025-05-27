package com.travel.controller;

import com.travel.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tour")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService;

    @PostMapping("/post/new")
    public ResponseEntity<Boolean> postTour(@RequestBody) {
    }
}
