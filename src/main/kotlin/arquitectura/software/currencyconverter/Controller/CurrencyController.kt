package arquitectura.software.currencyconverter.Controller

import arquitectura.software.currencyconverter.Service.CurrencyService
import arquitectura.software.currencyconverter.dao.Currency
import arquitectura.software.currencyconverter.dao.repository.CurrencyRepository
import arquitectura.software.currencyconverter.dto.ResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
    class CurrencyController @Autowired constructor(private val currencyService: CurrencyService){

    @GetMapping("/paginas")

    fun paginas(@RequestParam (defaultValue = "0") page: Int,
                @RequestParam (defaultValue = "5") size: Int,
                @RequestParam (defaultValue = "id") sort: String,
                @RequestParam (defaultValue = true.toString()) asc: Boolean):
            ResponseEntity<Page<Currency>> {
            var currencyPage = currencyService.paginas(
                PageRequest.of(page, size, Sort.by(sort)));
        if (!asc)
            currencyPage = currencyService.paginas(
                PageRequest.of(page, size, Sort.by(sort).descending()));
        return ResponseEntity<Page<Currency>>(currencyPage, HttpStatus.OK);
    }


}