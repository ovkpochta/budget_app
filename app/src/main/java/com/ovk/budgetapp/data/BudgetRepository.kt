package com.ovk.budgetapp.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class BudgetRepository(private val dao: BudgetDao) {
    val categories: Flow<List<CategoryEntity>> = dao.categoriesFlow()
    val accounts: Flow<List<AccountEntity>> = dao.accountsFlow()

    fun subcategories(categoryId: Long): Flow<List<SubcategoryEntity>> = dao.subcategoriesFlow(categoryId)

    suspend fun seedIfNeeded() {
        val materialsId = dao.insertCategory(CategoryEntity(name = "materials"))
        val worksId = dao.insertCategory(CategoryEntity(name = "works"))

        val allCategories = mutableListOf<Long>()
        if (materialsId > 0) allCategories += materialsId
        if (worksId > 0) allCategories += worksId

        val targetCategories = if (allCategories.isEmpty()) {
            // fallback if categories already existed
            listOf(1L, 2L)
        } else {
            allCategories
        }

        val subcategories = listOf(
            "кровля", "фундамент", "стены", "земельные работы", "дренаж",
            "наружная отделка", "внутренняя отделка", "отопление", "водоснабжение",
            "электричество", "сеть", "кухня", "санузлы"
        )

        targetCategories.forEach { categoryId ->
            subcategories.forEach { name ->
                dao.insertSubcategory(SubcategoryEntity(categoryId = categoryId, name = name))
            }
        }

        dao.insertAccount(AccountEntity(name = "Дебетовая карта", balance = 0.0))
        dao.insertAccount(AccountEntity(name = "Кредитная карта", balance = 0.0))
        dao.insertAccount(AccountEntity(name = "Наличные", balance = 0.0))
    }

    suspend fun addAccount(name: String, balance: Double) {
        dao.insertAccount(AccountEntity(name = name, balance = balance))
    }

    suspend fun addCategory(name: String) {
        dao.insertCategory(CategoryEntity(name = name))
    }

    suspend fun addSubcategory(categoryId: Long, name: String) {
        dao.insertSubcategory(SubcategoryEntity(categoryId = categoryId, name = name))
    }

    suspend fun addExpense(accountId: Long, amount: Double, date: LocalDate, categoryId: Long, subcategoryId: Long, comment: String) {
        dao.addExpense(accountId, amount, date.toString(), categoryId, subcategoryId, comment)
    }

    suspend fun addIncome(accountId: Long, amount: Double, date: LocalDate, comment: String) {
        dao.addIncome(accountId, amount, date.toString(), comment)
    }

    suspend fun addPlanned(operation: PlannedOperationEntity) {
        dao.insertPlannedOperation(operation)
    }

    suspend fun forecastBalance(onDate: LocalDate): Double {
        val current = dao.totalBalanceNow()
        val planned = dao.plannedUpToDate(onDate.toString())
        val delta = planned.sumOf { if (it.type == "INCOME") it.amount else -it.amount }
        return current + delta
    }
}
