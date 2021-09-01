package org.gern.marmot.notification

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.net.URL


class Emailer(
    private val client: OkHttpClient,
    private val sendgridUrl: URL,
    private val sendgridApiKey: String,
    private val fromAddress: String,
) {
    fun send(toAddress: String, subject: String, message: String): Boolean {
        val json = """
                {
                    "personalizations": [{"to":[{"email": "$toAddress"}]}],
                    "from": {"email": "$fromAddress"},
                    "subject": "$subject",
                    "content": [{
                        "type": "text/plain",
                        "value": "$message"
                    }]
                }""".trimIndent()


        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("$sendgridUrl/v3/mail/send")
            .addHeader("Authorization", "Bearer $sendgridApiKey")
            .post(body)
            .build()

        return client.newCall(request).execute().use(Response::isSuccessful)
    }
}
