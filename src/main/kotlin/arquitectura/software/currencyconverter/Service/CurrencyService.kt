package arquitectura.software.currencyconverter.Service

import arquitectura.software.currencyconverter.dao.Currency
import arquitectura.software.currencyconverter.dao.repository.CurrencyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CurrencyService @Autowired constructor(private val currencyRepository: CurrencyRepository){

    fun paginas(pageable: Pageable): Page<Currency> {
        return currencyRepository.findAll(pageable)
    }

}

//Convierte el metodo anteror a kotlin


