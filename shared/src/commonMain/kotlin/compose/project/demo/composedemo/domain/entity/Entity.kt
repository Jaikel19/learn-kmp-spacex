package compose.project.demo.composedemo.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class RocketLaunch(
    @SerialName("flight_number") val flightNumber: Int,
    @SerialName("name") val missionName: String,
    @SerialName("date_utc") val launchDateUTC: String,
    @SerialName("details") val details: String?,
    @SerialName("success") val launchSuccess: Boolean?,
    @SerialName("links") val links: Links,
) {
    var launchYear = 2025 // TODO: Default value, should be parsed from launchDateUTC
}

@Serializable
data class Links(
    @SerialName("patch") val patch: Patch?,
    @SerialName("article") val article: String?,
)

@Serializable
data class Patch(@SerialName("small") val small: String?, @SerialName("large") val large: String?)