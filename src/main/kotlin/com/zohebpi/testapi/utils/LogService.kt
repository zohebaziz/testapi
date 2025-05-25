package com.zohebpi.testapi.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface LoggerService {
  fun info(msg: String)
  fun debug(msg: String)
  fun warn(msg: String)
  fun error(msg: String, throwable: Throwable? = null)
}

class LoggerServiceImpl<T : Any>(private val clazz: Class<T>) : LoggerService {
  private val logger: Logger = LoggerFactory.getLogger(clazz)

  override fun info(msg: String) = logger.info(msg)
  override fun debug(msg: String) = logger.debug(msg)
  override fun warn(msg: String) = logger.warn(msg)
  override fun error(msg: String, throwable: Throwable?) =
    if (throwable != null) logger.error(msg, throwable) else logger.error(msg)
}

inline fun <reified T : Any> loggerService(): LoggerService = LoggerServiceImpl(T::class.java)
