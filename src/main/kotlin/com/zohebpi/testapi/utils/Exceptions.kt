package com.zohebpi.testapi.utils

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ErrorResponse(val status: Int, val error: String, val message: String?)

@ControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException::class)
  fun handleNotFound(e: NotFoundException): ResponseEntity<ErrorResponse> =
    ResponseEntity(ErrorResponse(404, "Not Found", e.message), HttpStatus.NOT_FOUND)

  @ExceptionHandler(BadRequestException::class)
  fun handleBadRequest(e: BadRequestException): ResponseEntity<ErrorResponse> =
    ResponseEntity(ErrorResponse(400, "Bad Request", e.message), HttpStatus.BAD_REQUEST)

  @ExceptionHandler(UnauthorizedException::class)
  fun handleUnauthorized(e: UnauthorizedException): ResponseEntity<ErrorResponse> =
    ResponseEntity(ErrorResponse(401, "Unauthorized", e.message), HttpStatus.UNAUTHORIZED)

  @ExceptionHandler(ForbiddenException::class)
  fun handleForbidden(e: ForbiddenException): ResponseEntity<ErrorResponse> =
    ResponseEntity(ErrorResponse(403, "Forbidden", e.message), HttpStatus.FORBIDDEN)

  @ExceptionHandler(InternalServerErrorException::class)
  fun handleInternalServerError(e: InternalServerErrorException): ResponseEntity<ErrorResponse> =
    ResponseEntity(ErrorResponse(500, "Internal Server Error", e.message), HttpStatus.INTERNAL_SERVER_ERROR)
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String?) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException(message: String?) : RuntimeException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedException(message: String?) : RuntimeException(message)

@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(message: String?) : RuntimeException(message)

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class InternalServerErrorException(message: String?) : RuntimeException(message)