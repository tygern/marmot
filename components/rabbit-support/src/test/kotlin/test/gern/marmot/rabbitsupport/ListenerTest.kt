package test.gern.marmot.rabbitsupport

import com.rabbitmq.client.MessageProperties
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runBlockingTest
import org.gern.marmot.rabbitsupport.buildConnectionFactory
import org.gern.marmot.rabbitsupport.declare
import org.gern.marmot.rabbitsupport.listen
import org.gern.marmot.rabbitsupport.useChannel
import org.junit.After
import org.junit.Before
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class ListenerTest {
    private val testQueue = "test-queue"
    private val testExchange = "test-exchange"
    private val factory = buildConnectionFactory(URI("amqp://localhost:5672"))

    @Before
    fun setUp() {
        factory.declare(exchange = testExchange, queue = testQueue)
    }

    @After
    fun tearDown() {
        factory.useChannel { channel ->
            channel.queueDelete(testQueue)
            channel.exchangeDelete(testExchange)
        }
    }

    @Test
    fun testListen() = runBlockingTest {
        factory.useChannel { channel ->
            channel.basicPublish(testExchange, "", MessageProperties.PERSISTENT_BASIC, "hi there".toByteArray())

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
