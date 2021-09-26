package test.initialcapacity.emailverifier.testsupport

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.test.assertTrue

fun assertJsonEquals(expected: String, actual: String?) {
    val mapper = jacksonObjectMapper()

    val expectedJson = mapper.readTree(expected)
    val actualJson = mapper.readTree(actual)

    assertTrue(
        expectedJson.equals(actualJson),
        "Expected\n${expectedJson.toPrettyString()}\n to equal \n${actualJson.toPrettyString()}\n"
    )
}