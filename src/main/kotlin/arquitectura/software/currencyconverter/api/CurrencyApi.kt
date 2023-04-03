package arquitectura.software.currencyconverter.api

import arquitectura.software.currencyconverter.dao.Currency
import arquitectura.software.currencyconverter.dto.ResponseServiceDto
import arquitectura.software.currencyconverter.proxy.CurrencyApiProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RequestMapping("/api/currency")
@RestController
class CurrencyApi @Autowired constructor(private val currencyApiProxy: CurrencyApiProxy) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(CurrencyApi::class.java)
    }

    @GetMapping("/exchange")
    fun exchangeRate(@RequestParam to: String,
                     @RequestParam from: String,
                     @RequestParam amount: BigDecimal): ResponseServiceDto {
        LOGGER.info("Iniciando peticion para convertir divisas de $from a $to con un monto de $amount")
        val result = currencyApiProxy.exchangeRate(to, from, amount)
        return result!!
    }

    @GetMapping("/paginas")
    fun paginas(@RequestParam page: Int,
                @RequestParam size: Int,
                @RequestParam sort: String,
                @RequestParam asc: Boolean):
            ResponseEntity<Page<Currency>> {
                LOGGER.info("Iniciando peticion para obtener la lista de conversiones")
                var currencyPage = currencyApiProxy.paginas(
                    PageRequest.of(page, size, Sort.by(sort)));
                if (!asc)
                    currencyPage = currencyApiProxy.paginas(
                        PageRequest.of(page, size, Sort.by(sort).descending()));
                return ResponseEntity<Page<Currency>>(currencyPage, HttpStatus.OK);
            }

}