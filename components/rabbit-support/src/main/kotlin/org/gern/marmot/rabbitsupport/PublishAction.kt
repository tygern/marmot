package org.gern.marmot.rabbitsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

typealias PublishAction = (String) -> Unit

fun publish(factory: ConnectionFactory, exchange: String): PublishAction = fun(message: String) =
    factory.useChannel { channel ->
        channel.basicPublish(exchange, "", MessageProperties.PERSISTENT_BASIC, message.toByteArray())
    }
