package compose.project.demo.composedemo.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RemoteRocketLaunchesDataSourceTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `latestLaunches should return list of rocket launches on success`() = runTest {
        // Arrange
        val mockResponse = """
      [
        {
          "flight_number": 1,
          "name": "FalconSat",
          "date_utc": "2006-03-24T22:30:00.000Z",
          "details": "Engine failure at 33 seconds and loss of vehicle",
          "success": false,
          "links": {
            "patch": {
              "small": "https://images2.imgbox.com/3c/0e/T8iJcSN3_o.png",
              "large": "https://images2.imgbox.com/40/e3/GypSkayF_o.png"
            },
            "article": "https://www.space.com/2196-spacex-inaugural-falcon-1-rocket-lost-launch.html"
          }
        },
        {
          "flight_number": 2,
          "name": "DemoSat",
          "date_utc": "2007-03-21T01:10:00.000Z",
          "details": "Successful first stage burn and transition to second stage",
          "success": true,
          "links": {
            "patch": {
              "small": "https://images2.imgbox.com/4f/e3/I0lkuJ2e_o.png",
              "large": "https://images2.imgbox.com/3d/86/cnu0pan8_o.png"
            },
            "article": null
          }
        }
      ]
    """.trimIndent()

        val mockEngine = MockEngine { request ->
            assertEquals("https://api.spacexdata.com/v5/launches", request.url.toString())
            respond(
                content = mockResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val dataSource = RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined
        )

        // Act
        val result = dataSource.latestLaunches().first()

        // Assert
        assertEquals(2, result.size)

        assertEquals(1, result[0].flightNumber)
        assertEquals("FalconSat", result[0].missionName)
        assertEquals("2006-03-24T22:30:00.000Z", result[0].launchDateUTC)
        assertEquals("Engine failure at 33 seconds and loss of vehicle", result[0].details)
        assertEquals(false, result[0].launchSuccess)
        assertEquals("https://images2.imgbox.com/3c/0e/T8iJcSN3_o.png", result[0].links.patch?.small)
        assertEquals("https://images2.imgbox.com/40/e3/GypSkayF_o.png", result[0].links.patch?.large)
        assertEquals("https://www.space.com/2196-spacex-inaugural-falcon-1-rocket-lost-launch.html", result[0].links.article)

        assertEquals(2, result[1].flightNumber)
        assertEquals("DemoSat", result[1].missionName)
        assertEquals("2007-03-21T01:10:00.000Z", result[1].launchDateUTC)
        assertEquals("Successful first stage burn and transition to second stage", result[1].details)
        assertEquals(true, result[1].launchSuccess)
        assertEquals("https://images2.imgbox.com/4f/e3/I0lkuJ2e_o.png", result[1].links.patch?.small)
        assertEquals("https://images2.imgbox.com/3d/86/cnu0pan8_o.png", result[1].links.patch?.large)
        assertEquals(null, result[1].links.article)

        httpClient.close()
    }

    @Test
    fun `latestLaunches should return empty list when API returns empty array`() = runTest {
        // Arrange
        val mockResponse = "[]"

        val mockEngine = MockEngine { request ->
            respond(
                content = mockResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val dataSource = RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined
        )

        // Act
        val result = dataSource.latestLaunches().first()

        // Assert
        assertEquals(0, result.size)

        httpClient.close()
    }

    @Test
    fun `latestLaunches should throw exception when API returns error`() = runTest {
        // Arrange
        val mockEngine = MockEngine { request ->
            respond(
                content = "Internal Server Error",
                status = HttpStatusCode.InternalServerError,
                headers = headersOf(HttpHeaders.ContentType, "text/plain")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val dataSource = RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined
        )

        // Act & Assert
        assertFailsWith<Exception> {
            dataSource.latestLaunches().first()
        }

        httpClient.close()
    }

    @Test
    fun `latestLaunches should handle nullable fields correctly`() = runTest {
        // Arrange
        val mockResponse = """
      [
        {
          "flight_number": 3,
          "name": "Trailblazer",
          "date_utc": "2008-08-03T03:34:00.000Z",
          "details": null,
          "success": null,
          "links": {
            "patch": null,
            "article": null
          }
        }
      ]
    """.trimIndent()

        val mockEngine = MockEngine { request ->
            respond(
                content = mockResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }

        val dataSource = RemoteRocketLaunchesDataSource(
            httpClient = httpClient,
            ioDispatcher = Dispatchers.Unconfined
        )

        // Act
        val result = dataSource.latestLaunches().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals(3, result[0].flightNumber)
        assertEquals("Trailblazer", result[0].missionName)
        assertEquals("2008-08-03T03:34:00.000Z", result[0].launchDateUTC)
        assertEquals(null, result[0].details)
        assertEquals(null, result[0].launchSuccess)
        assertEquals(null, result[0].links.patch)
        assertEquals(null, result[0].links.article)

        httpClient.close()
    }
}