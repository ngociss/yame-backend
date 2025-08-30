package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Hello World Endpoint", description = "Returns a simple Hello, World! message")
    @Tag(name = "Tests")
    public String hello() {
        return "Hello, World!";
    }
}
