package com.zohebpi.testapi.controllers

import com.zohebpi.testapi.dtos.DeploymentResponseDto
import com.zohebpi.testapi.dtos.DeploymentStatus
import com.zohebpi.testapi.utils.BadRequestException
import com.zohebpi.testapi.utils.ForbiddenException
import com.zohebpi.testapi.utils.InternalServerErrorException
import com.zohebpi.testapi.utils.UnauthorizedException
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val DEPLOY_SECRET = "\${deploy.secret}"

@RestController
@RequestMapping("/v1/deploy")
class DeploymentController(
  @Value(DEPLOY_SECRET) private val deploySecret: String?
) {

  @PostMapping
  fun deploy(@RequestHeader("x-deploy-token") deployToken: String?): DeploymentResponseDto {
    validateDeploymentToken(deployToken)

    val home = System.getProperty("user.home")
    val scriptPath = "$home/Documents/scripts/deploy-testapi.sh"

    return try {
      val process = ProcessBuilder("bash", scriptPath)
        .redirectErrorStream(true)
        .start()

      process.inputStream.bufferedReader().readText()
      val exitCode = process.waitFor()

      if (exitCode == 0) {
        DeploymentResponseDto(DeploymentStatus.SUCCESS)
      } else {
        throw InternalServerErrorException("Unhandled exception during deployment")
      }
    } catch (e: Exception) {
      throw InternalServerErrorException("Unhandled exception during deployment")
    }
  }

  private fun validateDeploymentToken(token: String?) {
    if (token.isNullOrEmpty()) throw BadRequestException("Missing or blank deployment key header")
    if (token != getDeploymentSecret()) throw ForbiddenException("Invalid deployment key")
  }

  private fun getDeploymentSecret(): String {
    return deploySecret ?: throw UnauthorizedException("Missing/Invalid deployment key configuration")
  }
}