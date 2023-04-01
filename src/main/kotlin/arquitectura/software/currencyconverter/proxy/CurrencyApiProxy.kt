package arquitectura.software.currencyconverter.proxy

import arquitectura.software.currencyconverter.bl.CurrencyBl
import arquitectura.software.currencyconverter.dao.repository.CurrencyRepository
import arquitectura.software.currencyconverter.dto.ResponseServiceDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CurrencyApiProxy(private val currencyBl: CurrencyBl, currencyRepository: CurrencyRepository) :
    CurrencyBl(currencyRepository) {

    companion object {
        const val MAX_REQUESTS_PER_SECOND = 1
        const val REQUESTS_INTERVAL_MS = 3000L
        private val LOGGER: Logger = LoggerFactory.getLogger(CurrencyApiProxy::class.java)
    }

    private var requestsCounter = 0L
    private var lastRequestTimestamp = System.currentTimeMillis()

    override fun exchangeRate(to: String, from: String, amount: BigDecimal): ResponseServiceDto? {
        synchronized(this) {
            val currentTimestamp = System.currentTimeMillis()
            val elapsedMilliseconds = currentTimestamp - lastRequestTimestamp
            LOGGER.info("Iniciando proxy para convertir divisas de $from a $to con un monto de $amount")
            LOGGER.info("elapsedMilliseconds: $elapsedMilliseconds")
            if (elapsedMilliseconds < REQUESTS_INTERVAL_MS) {
                requestsCounter++

                if (requestsCounter > MAX_REQUESTS_PER_SECOND) {
                    LOGGER.info("Se ha superado el limite de peticiones por segundo")
                    return null
                }
            } else {
                lastRequestTimestamp = currentTimestamp
                requestsCounter = 1
            }
        }

        return currencyBl.exchangeRate(to, from, amount)
    }
}