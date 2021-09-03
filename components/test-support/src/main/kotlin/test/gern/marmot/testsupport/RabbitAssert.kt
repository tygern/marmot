package test.gern.marmot.testsupport

import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.GetResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import org.gern.marmot.rabbitsupport.RabbitQueue
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.time.Duration

suspend fun ConnectionFactory.assertMessageReceived(
    queue: RabbitQueue,
    message: String,
    timeout: Duration = Duration.milliseconds(50)
) {
    newConnection().use { connection ->
        connection.createChannel()!!.use { channel ->
            var received: GetResponse? = null
            var elapsed = Duration.ZERO
            val delayDuration = Duration.milliseconds(10)

            while (received == null) {
                if (elapsed >= timeout) {
                    fail("No messages received")
                }
                delay(delayDuration)
                elapsed += delayDuration

                received = channel.basicGet(queue.name, true)
            }

            assertJsonEquals(received.body.decodeToString(), message)
        }
    }
}
