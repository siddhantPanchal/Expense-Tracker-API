package io.siddhant.expense_tracker.repositories

import io.siddhant.expense_tracker.models.Transaction
import io.siddhant.expense_tracker.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    abstract fun findAllByUserAndCreateAtBetween(
        user: User,
        createAtAfter: LocalDateTime,
        createAtBefore: LocalDateTime
    ): List<Transaction>

    abstract fun findFirstByUserAndCreateAtBeforeOrderByCreateAtDesc(
        user: User,
        createAtBefore: LocalDateTime
    ): Transaction?

    abstract fun deleteByUserAndId(user: User, id: Long)


    fun findAllByUserAndCreateAtAfterOrderByCreateAtAsc(
        user: User,
        createAtBefore: LocalDateTime
    ): MutableList<Transaction>

    fun getReferenceByIdAndUser(id: Long, user: User): Transaction?
}