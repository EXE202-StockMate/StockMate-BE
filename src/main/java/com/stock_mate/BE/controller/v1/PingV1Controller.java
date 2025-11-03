package com.stock_mate.BE.controller.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ping", description = "Ping APIs")
@RestController
@RequestMapping("v1/ping")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PingV1Controller {
    @GetMapping
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Ping");
    }
}
