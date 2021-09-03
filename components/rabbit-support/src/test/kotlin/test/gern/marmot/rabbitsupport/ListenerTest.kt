package test.gern.marmot.rabbitsupport

import com.rabbitmq.client.MessageProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runBlockingTest
import org.gern.marmot.rabbitsupport.*
import org.junit.After
import org.junit.Before
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ListenerTest {
    private val testQueue = RabbitQueue("test-queue")
    private val testExchange = RabbitExchange("test-exchange")
    private val factory = buildConnectionFactory(URI("amqp://localhost:5672"))

    @Before
    fun setUp() {
        factory.declare(exchange = testExchange, queue = testQueue)
    }

    @After
    fun tearDown() {
        factory.useChannel { channel ->
            channel.queueDelete(testQueue.name)
            channel.exchangeDelete(testExchange.name)
        }
    }

    @Test
    fun testListen() = runBlockingTest {
        factory.useChannel { channel ->
            channel.basicPublish(testExchange.name, "", MessageProperties.PERSISTENT_BASIC, "hi there".toByteArray())

            val messageChannel = Channel<String>()
            listen(channel, testQueue) { message ->
                launch {
                    messageChannel.send(message)
                }
            }

            launch {
                val receivedMessage = messageChannel.receive()

                assertEquals("hi there", receivedMessage)
            }
        }
    }
}
