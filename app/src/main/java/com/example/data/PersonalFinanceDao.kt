package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalFinanceDao {
    // Budgets
    @Query("SELECT * FROM budgets ORDER BY monthlyLimitRial DESC")
    fun getAllBudgetsFlow(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // Cheques
    @Query("SELECT * FROM cheques ORDER BY dueDateTimestamp ASC")
    fun getAllChequesFlow(): Flow<List<ChequeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheque(cheque: ChequeEntity)

    @Update
    suspend fun updateCheque(cheque: ChequeEntity)

    @Delete
    suspend fun deleteCheque(cheque: ChequeEntity)

    // Loans
    @Query("SELECT * FROM loans ORDER BY nextDueTimestamp ASC")
    fun getAllLoansFlow(): Flow<List<LoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Delete
    suspend fun deleteLoan(loan: LoanEntity)

    // Goals
    @Query("SELECT * FROM savings_goals ORDER BY deadlineTimestamp ASC")
    fun getAllGoalsFlow(): Flow<List<SavingsGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoalEntity)

    @Update
    suspend fun updateGoal(goal: SavingsGoalEntity)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoalEntity)
}
