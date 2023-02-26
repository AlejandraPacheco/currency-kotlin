package arquitectura.software.currencyconverter.dao.repository

import arquitectura.software.currencyconverter.dao.Currency
import org.springframework.data.repository.CrudRepository

interface CurrencyRepository: CrudRepository<Currency, Long>