package test.initialcapacity.emailverifier.rabbitsupport

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import io.initialcapacity.emailverifier.rabbitsupport.*
import org.junit.After
import org.junit.Before
import test.initialcapacity.emailverifier.testsupport.assertMessageReceived
import java.net.URI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

class TestPublishAction {
    private val testQueue = RabbitQueue("test-queue")
    private val testExchange = RabbitExchange("test-exchange")
    private val factory = buildConnectionFactory(URI("amqp://localhost:5672"))

    @Before
    fun setUp() {
        factory.declare(testExchange, testQueue)
    }

    @After
    fun tearDown() {
        factory.useChannel { channel ->
            channel.queueDelete(testQueue.name)
            channel.exchangeDelete(testExchange.name)
        }
    }

    @Test
    fun testPublish() = runBlocking {
        val publishAction = publish(factory, testExchange)

        publishAction("""{"some": "message"}""")

        factory.assertMessageReceived(testQueue, """{"some": "message"}""")
    }
}
