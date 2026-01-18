package com.thurman.pingpong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/ping")
public class PingPongController {

    @GetMapping
    public String pingPong() {
        return "pong";
    }

}
