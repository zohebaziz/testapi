package com.zohebpi.testapi.dtos

enum class DeploymentStatus(
  val value: String
) {
  SUCCESS("success"),
  FAILURE("failure")
  ;

  fun get(): String {
    return this.value
  }
}

data class DeploymentResponseDto(
  val deploymentStatus: DeploymentStatus
)