package org.gern.marmot.rabbitsupport

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import java.net.URI

fun buildConnectionFactory(rabbitUrl: URI): ConnectionFactory =
    ConnectionFactory().apply {
        setUri(rabbitUrl)
    }

fun ConnectionFactory.declare(exchange: String, queue: String): Unit =
    useChannel {
        it.exchangeDeclare(exchange, "direct", false, false, null)
        it.queueDeclare(queue, false, false, false, null)
        it.queueBind(queue, exchange, "")
    }

fun <T> ConnectionFactory.useChannel(block: (Channel) -> T): T =
    newConnection().use { connection ->
        connection.createChannel()!!.use(block)
    }