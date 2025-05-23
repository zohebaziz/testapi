package com.zohebpi.testapi.controllers

import com.zohebpi.testapi.dtos.VersionDto
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController (
  private val buildProperties: BuildProperties
) {

  @GetMapping("/version")
  fun getVersion(): VersionDto {
    return VersionDto(buildProperties.version)
  }
}