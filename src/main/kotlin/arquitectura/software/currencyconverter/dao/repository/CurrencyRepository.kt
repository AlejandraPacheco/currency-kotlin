package arquitectura.software.currencyconverter.dao.repository

import arquitectura.software.currencyconverter.dao.Currency
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CurrencyRepository: JpaRepository<Currency, Int>