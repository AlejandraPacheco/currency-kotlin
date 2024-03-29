package arquitectura.software.currencyconverter.bl

import arquitectura.software.currencyconverter.dao.repository.CurrencyRepository
import arquitectura.software.currencyconverter.dao.Currency
import arquitectura.software.currencyconverter.dto.ErrorServiceDto
import arquitectura.software.currencyconverter.dto.ResponseServiceDto
import arquitectura.software.currencyconverter.exception.ServiceException
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.math.BigDecimal
import org.springframework.beans.factory.annotation.Value
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

@Service
class CurrencyBl @Autowired constructor(private val currencyRepository: CurrencyRepository){

    companion object {
        val objectMapper = jacksonObjectMapper()
        val LOGGER: Logger = LoggerFactory.getLogger(CurrencyBl::class.java)
    }

    @Value("\${api.url}")
    lateinit var apiUrl: String

    @Value("\${api.key}")
    lateinit var apiKey: String

    fun exchangeRate(to: String, from: String, amount: BigDecimal): ResponseServiceDto? {
        LOGGER.info("Iniciando logica para convertir divisas")
        if (amount < BigDecimal.ZERO) {
            LOGGER.error("El monto no puede ser negativo")
            throw IllegalArgumentException("El monto no puede ser negativo")
        }
        val response = invokeApi("$apiUrl?to=$to&from=$from&amount=$amount")
        val responseServiceDto = parseResponse(response)
        val currency = Currency()
        currency.currencyFrom = from
        currency.currencyTo = to
        currency.amount = amount
        currency.date = Date()
        currency.result = responseServiceDto.result!!
        currencyRepository.save(currency)
        return responseServiceDto
    }

    fun invokeApi(endpoint: String): Response {
        LOGGER.info("Invocando servicio de conversión de monedas")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(endpoint)
            .addHeader("apikey", apiKey)
            .build()
        try {
            return client.newCall(request).execute()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException("Error en el servicio de conversión de monedas")
        }
    }

    fun parseResponse(response: Response): ResponseServiceDto {
        LOGGER.info("Parseando respuesta del servicio de conversión de monedas")
        val body = response.body().string()
        LOGGER.info("El servicio de conversión de monedas retorno => $body")
        if(response.isSuccessful) {
            LOGGER.info("El servicio de conversión de monedas fue exitoso")
            return objectMapper.readValue(body)
        }
        LOGGER.info("El servicio de conversión de monedas fue fallido")
        val errorService = objectMapper.readValue<ErrorServiceDto>(body)
        throw ServiceException("Code: ${errorService.error.code}, message: ${errorService.error.message}")
    }

    fun paginas(pageable: Pageable): Page<Currency> {
        return currencyRepository.findAll(pageable)
    }
}