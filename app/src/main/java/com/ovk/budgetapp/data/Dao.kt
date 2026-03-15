package com.ovk.budgetapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubcategory(subcategory: SubcategoryEntity): Long

    @Query("SELECT * FROM categories ORDER BY name")
    fun categoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM subcategories WHERE categoryId = :categoryId ORDER BY name")
    fun subcategoriesFlow(categoryId: Long): Flow<List<SubcategoryEntity>>

    @Query("SELECT * FROM subcategories ORDER BY name")
    fun allSubcategoriesFlow(): Flow<List<SubcategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Query("SELECT * FROM accounts ORDER BY name")
    fun accountsFlow(): Flow<List<AccountEntity>>

    @Query("UPDATE accounts SET balance = balance + :delta WHERE id = :accountId")
    suspend fun updateAccountBalance(accountId: Long, delta: Double)

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Insert
    suspend fun insertPlannedOperation(operation: PlannedOperationEntity)

    @Query("SELECT * FROM planned_operations WHERE plannedDate <= :date")
    suspend fun plannedUpToDate(date: String): List<PlannedOperationEntity>

    @Query("SELECT COALESCE(SUM(balance), 0.0) FROM accounts")
    suspend fun totalBalanceNow(): Double

    @Transaction
    suspend fun addExpense(accountId: Long, amount: Double, date: String, categoryId: Long, subcategoryId: Long, comment: String) {
        updateAccountBalance(accountId, -amount)
        insertTransaction(
            TransactionEntity(
                accountId = accountId,
                amount = amount,
                date = date,
                type = "EXPENSE",
                categoryId = categoryId,
                subcategoryId = subcategoryId,
                comment = comment
            )
        )
    }

    @Transaction
    suspend fun addIncome(accountId: Long, amount: Double, date: String, comment: String) {
        updateAccountBalance(accountId, amount)
        insertTransaction(
            TransactionEntity(
                accountId = accountId,
                amount = amount,
                date = date,
                type = "INCOME",
                categoryId = null,
                subcategoryId = null,
                comment = comment
            )
        )
    }
}
