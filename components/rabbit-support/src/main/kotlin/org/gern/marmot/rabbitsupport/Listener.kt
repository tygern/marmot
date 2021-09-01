package org.gern.marmot.rabbitsupport

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Delivery
import kotlinx.coroutines.*
import kotlin.time.Duration


fun listen(channel: Channel, queue: String, handler: (String) -> Unit): String {
    val delivery = { _: String, message: Delivery -> handler(message.body.decodeToString()) }
    val cancel = { _: String -> }

    return channel.basicConsume(queue, true, delivery, cancel)
}
