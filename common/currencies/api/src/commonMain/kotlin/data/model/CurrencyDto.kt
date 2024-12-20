package data.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDto(
    val currencyCode: String,
    val currencyName: String,
    val flagImageUrl: String,
    val usdRate : Double,
    val fetchTimeInstant: Instant = Clock.System.now()
)
