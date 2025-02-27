package io.siddhant.expense_tracker.services

import io.siddhant.expense_tracker.models.Transaction
import io.siddhant.expense_tracker.models.TransactionType
import io.siddhant.expense_tracker.models.User
import io.siddhant.expense_tracker.repositories.TransactionRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TransactionService(val transactionRepository: TransactionRepository) {


    fun createTransaction(
        name: String,
        description: String?,
        amount: Double,
        transactionType: TransactionType,
        user: User
    ): Transaction {
        val lastTransaction = getLastTransaction(user);
        val currentBalance = balance(lastTransaction?.balance, amount, transactionType);
        val currentTransaction = Transaction(name, description, amount, currentBalance, transactionType, user)
        return transactionRepository.save(currentTransaction);
    }

    private fun getLastTransaction(user: User, createAtBefore: LocalDateTime = LocalDateTime.now()): Transaction? {
        return transactionRepository.findFirstByUserAndCreateAtBeforeOrderByCreateAtDesc(user, createAtBefore);
    }

    fun getAllTransactions(user: User, from: LocalDateTime, to: LocalDateTime): List<Transaction> {
        return transactionRepository.findAllByUserAndCreateAtBetween(user, from, to);
    }

    fun getAllTransactionsAfter(user: User, after: LocalDateTime): List<Transaction> {
        return transactionRepository.findAllByUserAndCreateAtAfterOrderByCreateAtAsc(user, after);
    }


    private fun balance(previousBalance: Double?, amount: Double, transactionType: TransactionType): Double {
        return when (transactionType) {
            TransactionType.EXPENSE -> (previousBalance ?: 0.0) - amount
            TransactionType.INCOME -> (previousBalance ?: 0.0) + amount
        }
    }

    private fun previousBalance(transaction: Transaction): Double {
        return when (transaction.transactionType!!) {
            TransactionType.EXPENSE -> transaction.balance!! + transaction.amount!!
            TransactionType.INCOME -> transaction.balance!! - transaction.amount!!
        }
    }


    private fun isWithinRange(testDate: LocalDateTime, startDate: LocalDateTime, endDate: LocalDateTime): Boolean {
        return !(testDate.isBefore(startDate) || testDate.isAfter(endDate))
    }

    fun deleteTransaction(id: Long, user: User) {
        val transactionToBeDeleted = transactionRepository.getReferenceByIdAndUser(id, user)
            ?: throw Exception("Transaction not found");

        if (!isWithinRange(
                transactionToBeDeleted.createAt!!,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            )
        ) throw Exception("Transaction cannot be deleted, too old")

        var balance = previousBalance(transactionToBeDeleted);
        val modifiedTransactions = getAllTransactionsAfter(user, transactionToBeDeleted.createAt!!).map {
            balance = balance(
                balance,
                it.amount!!,
                it.transactionType!!
            )
            it.copy(balance = balance)
        }


        transactionRepository.saveAll(modifiedTransactions)
        transactionRepository.deleteById(id)
    }

    fun updateTransaction(id: Long, user: User, name: String?, description: String?, amount: Double?): Transaction {
        val transactionToBeUpdated = transactionRepository.getReferenceByIdAndUser(id, user)
            ?: throw Exception("Transaction not found");

        if (!isWithinRange(
                transactionToBeUpdated.createAt!!,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            )
        ) throw Exception("Transaction cannot be updated, too old")

        lateinit var updatedTransaction: Transaction
        if (amount != null && transactionToBeUpdated.amount != amount) {
            updatedTransaction = updateTransactionBalance(id, user, amount)
        }
        if (transactionToBeUpdated.name != name || transactionToBeUpdated.description != description) {
            transactionToBeUpdated.name = name ?: transactionToBeUpdated.name;
            transactionToBeUpdated.description = description ?: transactionToBeUpdated.description;
            updatedTransaction = transactionToBeUpdated.copy(
                name = name ?: transactionToBeUpdated.name!!,
                description = description ?: transactionToBeUpdated.description!!
            )
        }
        updatedTransaction.updatedAt = LocalDateTime.now();
        transactionRepository.save(updatedTransaction)
        return updatedTransaction;
    }

    private fun updateTransactionBalance(id: Long, user: User, amount: Double): Transaction {
        val transactionToBeUpdated = transactionRepository.getReferenceByIdAndUser(id, user)
            ?: throw Exception("Transaction not found");

        if (!isWithinRange(
                transactionToBeUpdated.createAt!!,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now()
            )
        ) throw Exception("Transaction cannot be updated, too old")


        val previousTransactionBalance = previousBalance(transactionToBeUpdated);
//        transactionToBeUpdated.balance =
//            balance(previousTransactionBalance, amount, transactionToBeUpdated.transactionType!!);
//
        var newBalance = balance(previousTransactionBalance, amount, transactionToBeUpdated.transactionType!!);
        transactionToBeUpdated.balance = newBalance
        transactionToBeUpdated.amount = amount

        val modifiedTransactions: List<Transaction> =
            listOf(transactionToBeUpdated, *getAllTransactionsAfter(user, transactionToBeUpdated.createAt!!).map {
                newBalance = balance(
                    newBalance,
                    it.amount!!,
                    it.transactionType!!
                )
                it.copy(balance = newBalance)
            }.toTypedArray())



        transactionRepository.saveAll(modifiedTransactions)

        return transactionToBeUpdated
    }


}