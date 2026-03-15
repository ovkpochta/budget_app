package com.ovk.budgetapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ovk.budgetapp.data.AppDatabase
import com.ovk.budgetapp.data.BudgetRepository
import com.ovk.budgetapp.data.PlannedOperationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BudgetRepository(AppDatabase.get(application).budgetDao())

    val categories = repository.categories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val accounts = repository.accounts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _forecast = MutableStateFlow<Double?>(null)
    val forecast: StateFlow<Double?> = _forecast

    init {
        viewModelScope.launch { repository.seedIfNeeded() }
    }

    fun addAccount(name: String, balance: Double) = viewModelScope.launch {
        repository.addAccount(name, balance)
    }

    fun addCategory(name: String) = viewModelScope.launch {
        repository.addCategory(name)
    }

    fun addExpense(accountId: Long, amount: Double, categoryId: Long, subcategoryId: Long, comment: String) = viewModelScope.launch {
        repository.addExpense(accountId, amount, LocalDate.now(), categoryId, subcategoryId, comment)
    }

    fun addIncome(accountId: Long, amount: Double, comment: String) = viewModelScope.launch {
        repository.addIncome(accountId, amount, LocalDate.now(), comment)
    }

    fun addPlannedExpense(accountId: Long, amount: Double, date: LocalDate, categoryId: Long, subcategoryId: Long, comment: String) = viewModelScope.launch {
        repository.addPlanned(
            PlannedOperationEntity(
                accountId = accountId,
                amount = amount,
                plannedDate = date.toString(),
                type = "EXPENSE",
                categoryId = categoryId,
                subcategoryId = subcategoryId,
                comment = comment
            )
        )
    }

    fun addPlannedIncome(accountId: Long, amount: Double, date: LocalDate, comment: String) = viewModelScope.launch {
        repository.addPlanned(
            PlannedOperationEntity(
                accountId = accountId,
                amount = amount,
                plannedDate = date.toString(),
                type = "INCOME",
                categoryId = null,
                subcategoryId = null,
                comment = comment
            )
        )
    }

    fun calculateForecast(date: LocalDate) = viewModelScope.launch {
        _forecast.value = repository.forecastBalance(date)
    }
}
