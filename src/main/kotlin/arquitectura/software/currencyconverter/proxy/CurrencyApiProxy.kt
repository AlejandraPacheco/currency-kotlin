package arquitectura.software.currencyconverter.proxy

import arquitectura.software.currencyconverter.bl.CurrencyBl
import arquitectura.software.currencyconverter.dao.repository.CurrencyRepository
import arquitectura.software.currencyconverter.dto.RequestServiceDto
import arquitectura.software.currencyconverter.dto.ResponseServiceDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component
class CurrencyApiProxy(private val currencyBl: CurrencyBl, currencyRepository: CurrencyRepository) :
    CurrencyBl(currencyRepository) {

    companion object {
        const val MAX_REQUESTS_PER_SECOND = 10
        const val REQUESTS_INTERVAL_MS = 2000
        private val LOGGER: Logger = LoggerFactory.getLogger(CurrencyApiProxy::class.java)
    }

    private var requestsCounter = 0
    private var lastRequestTimestamp = System.currentTimeMillis()
    private var isProcessingRequest = false
    private val pendingRequests = LinkedList<RequestServiceDto>()

    override fun exchangeRate(to: String, from: String, amount: BigDecimal): ResponseServiceDto {
        // Agrega la solicitud actual a la cola de solicitudes pendientes
        val request = RequestServiceDto(to, from, amount)
        LOGGER.info("Agregando solicitud a la cola de solicitudes pendientes: $request")
        pendingRequests.add(request)

        // Verifica si hay solicitudes pendientes y las procesa si es posible
        processPendingRequests()

        // Espera hasta que se procese la solicitud actual
        while (isProcessingRequest) {
            Thread.sleep(100)
        }

        // Procesa la solicitud actual
        requestsCounter++
        val currentTimestamp = System.currentTimeMillis()
        val elapsedMilliseconds = currentTimestamp - lastRequestTimestamp
        if (elapsedMilliseconds < REQUESTS_INTERVAL_MS) {
            Thread.sleep(REQUESTS_INTERVAL_MS - elapsedMilliseconds)
        }
        lastRequestTimestamp = currentTimestamp

        val result: ResponseServiceDto? = currencyBl.exchangeRate(to, from, amount)

        // Marca la solicitud actual como procesada y verifica si hay solicitudes pendientes
        isProcessingRequest = false
        processPendingRequests()

        return result!!
    }

    private fun processPendingRequests() {
        // Verifica si hay solicitudes pendientes y las procesa si es posible
        while (pendingRequests.isNotEmpty() && !isProcessingRequest && requestsCounter < MAX_REQUESTS_PER_SECOND) {
            val request = pendingRequests.remove()

            // Procesa la siguiente solicitud pendiente
            requestsCounter++
            val currentTimestamp = System.currentTimeMillis()
            val elapsedMilliseconds = currentTimestamp - lastRequestTimestamp
            if (elapsedMilliseconds < REQUESTS_INTERVAL_MS) {
                LOGGER.info("Esperando para procesar solicitud pendiente")
                pendingRequests.addFirst(request)
                break
            }
            lastRequestTimestamp = currentTimestamp
            val result = currencyBl.exchangeRate(request.to, request.from, request.amount)
            LOGGER.info("Resultado de la solicitud pendiente: $result")
        }
    }
}