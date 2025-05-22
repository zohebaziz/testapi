package com.zohebpi.testapi.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/test")
class TestApiController {

  @GetMapping("/hello")
  fun helloWord(): String {
    return "Test Api Says Hello!"
  }
}