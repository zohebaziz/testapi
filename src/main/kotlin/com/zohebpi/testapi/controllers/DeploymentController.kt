package com.zohebpi.testapi.controllers

import com.zohebpi.testapi.dtos.DeploymentResponseDto
import com.zohebpi.testapi.dtos.DeploymentStatus
import com.zohebpi.testapi.utils.BadRequestException
import com.zohebpi.testapi.utils.ForbiddenException
import com.zohebpi.testapi.utils.NotFoundException
import com.zohebpi.testapi.utils.UnauthorizedException
import com.zohebpi.testapi.utils.loggerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

private const val DEPLOY_SECRET = "\${deploy.secret}"
private const val DEPLOY_SCRIPT_PATH = "\${deploy.script.path}"

@RestController
@RequestMapping("/v1/deploy")
class DeploymentController(
  @Value(DEPLOY_SECRET) private val deploySecret: String,
  @Value(DEPLOY_SCRIPT_PATH) private val deployScriptPath: String
) {
  private val logger = loggerService<DeploymentController>()

  @PostMapping
  @RequestMapping("/{serviceName}")
  fun deploy(
    @RequestHeader("x-deploy-token",) deployToken: String?,
    @PathVariable serviceName: String
  ): DeploymentResponseDto {
    validateDeploymentToken(deployToken)

    val jobId = UUID.randomUUID().toString()
    setStatus(jobId, DeploymentStatus.RUNNING)

    logger.info("Deployment begun... Job Id: $jobId")

    Thread {
      try {
        val fullDeployScriptPath = "$deployScriptPath/deploy-$serviceName.sh"
        logger.info("Starting deploy script at path: $fullDeployScriptPath")

        val process = ProcessBuilder("bash", fullDeployScriptPath)
          .redirectErrorStream(true) // Combine stderr and stdout
          .start()

        // Read output stream and log it
        val reader = process.inputStream.bufferedReader()
        reader.forEachLine { line ->
          logger.info("[deploy script] $line")
        }

        val exitCode = process.waitFor()
        logger.info("Deploy script finished with exit code $exitCode")

        val status = if (exitCode == 0) DeploymentStatus.SUCCESS else DeploymentStatus.FAILURE
        setStatus(jobId, status)

      } catch (e: Exception) {
        logger.error("Deployment Failed for Job Id: $jobId. With exception stacktrace:", e)
        setStatus(jobId, DeploymentStatus.FAILURE)
      }
    }.start()


    return DeploymentResponseDto(DeploymentStatus.RUNNING, jobId)
  }

  @GetMapping("/status/{jobId}")
  fun getDeployStatus(@PathVariable jobId: String): DeploymentResponseDto {
    val status = getStatus(jobId)
      ?: throw NotFoundException("No job found with ID $jobId")
    return DeploymentResponseDto(status, jobId)
  }


  private fun validateDeploymentToken(token: String?) {
    if (token.isNullOrEmpty()) throw BadRequestException("Missing or blank deployment key header")
    if (token != deploySecret) throw ForbiddenException("Invalid deployment key")
  }

  companion object {
    private val jobs = mutableMapOf<String, DeploymentStatus>()

    fun setStatus(jobId: String, status: DeploymentStatus) {
      jobs[jobId] = status
    }

    fun getStatus(jobId: String): DeploymentStatus? = jobs[jobId]
  }
}