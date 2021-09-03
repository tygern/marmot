package org.gern.marmot.rabbitsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties

typealias PublishAction = (String) -> Unit
data class RabbitExchange(val name: String)

fun publish(factory: ConnectionFactory, exchange: RabbitExchange): PublishAction = fun(message: String) =
    factory.useChannel { channel ->
        channel.basicPublish(exchange.name, "", MessageProperties.PERSISTENT_BASIC, message.toByteArray())
    }
