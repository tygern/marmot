package test.gern.marmot.testsupport

import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.withTimeout
import kotlin.test.assertEquals
import kotlin.time.Duration

suspend fun ConnectionFactory.assertMessageReceived(
    queue: String,
    message: String,
    timeout: Duration = Duration.milliseconds(50)
) {
    withTimeout(timeout) {
        newConnection().use { connection ->
            connection.createChannel()!!.use { channel ->
                assertEquals(channel.basicGet(queue, true).body.decodeToString(), message)
            }
        }
    }
}
