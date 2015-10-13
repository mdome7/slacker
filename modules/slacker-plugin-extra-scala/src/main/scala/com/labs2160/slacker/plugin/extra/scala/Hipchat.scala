package com.labs2160.slacker.plugin.extra.scala

import java.util.Properties

import com.labs2160.slacker.api.Endpoint
import com.labs2160.slacker.api.SlackerResponse
import com.labs2160.slacker.api.SlackerException
import com.labs2160.slacker.api.SlackerContext

class HipChat extends Endpoint {

  @Override
  def setConfiguration(config: Properties): Unit = {
    // do nothing
  }

  @Override
  def deliverResponse(response: SlackerResponse): Boolean = {
    // do nothing
    true
  }
}
