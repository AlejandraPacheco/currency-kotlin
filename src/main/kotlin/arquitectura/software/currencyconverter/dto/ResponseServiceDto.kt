package arquitectura.software.currencyconverter.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResponseServiceDto (
    val success: Boolean,
    val query: RequestServiceDto,
    val date: String,
    val result: BigDecimal
)