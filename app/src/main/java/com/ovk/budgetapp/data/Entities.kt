package com.ovk.budgetapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(
    tableName = "subcategories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class SubcategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val name: String
)

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val balance: Double
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(entity = AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = SubcategoryEntity::class, parentColumns = ["id"], childColumns = ["subcategoryId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("accountId"), Index("categoryId"), Index("subcategoryId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val amount: Double,
    val date: String,
    val type: String,
    val categoryId: Long?,
    val subcategoryId: Long?,
    val comment: String = ""
)

@Entity(
    tableName = "planned_operations",
    foreignKeys = [
        ForeignKey(entity = AccountEntity::class, parentColumns = ["id"], childColumns = ["accountId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CategoryEntity::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.RESTRICT),
        ForeignKey(entity = SubcategoryEntity::class, parentColumns = ["id"], childColumns = ["subcategoryId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("accountId"), Index("categoryId"), Index("subcategoryId")]
)
data class PlannedOperationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountId: Long,
    val amount: Double,
    val plannedDate: String,
    val type: String,
    val categoryId: Long?,
    val subcategoryId: Long?,
    val comment: String = ""
)

fun LocalDate.toDbString(): String = this.toString()
