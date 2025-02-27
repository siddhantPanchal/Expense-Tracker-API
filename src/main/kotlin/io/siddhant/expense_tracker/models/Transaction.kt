package io.siddhant.expense_tracker.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "transaction")
class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "name", nullable = false, length = 45)
    open var name: String? = null

    @Column(name = "create_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    open var createAt: LocalDateTime? = null

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    open var updatedAt: LocalDateTime? = null

    @Column(name = "description")
    open var description: String? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    open var user: User? = null

    @Column(name = "amount", nullable = false)
    open var amount: Double? = null


    @Column(name = "balance", nullable = false)
    open var balance: Double? = null


    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    open var transactionType: TransactionType? = null
        protected set

    constructor(
        name: String,
        description: String?,
        amount: Double,
        balance: Double,
        transactionType: TransactionType,
        user: User
    ) {
        this.name = name
        this.description = description
        this.user = user
        this.amount = amount
        this.balance = balance
        this.transactionType = transactionType
        this.createAt = LocalDateTime.now()
    }

    fun copy(
        name: String = this.name!!,
        description: String = this.description!!,
        amount: Double = this.amount!!,
        balance: Double = this.balance!!,
        updatedAt: LocalDateTime? = this.updatedAt,
    ): Transaction {
        val transaction = Transaction(
            name,
            description,
            amount,
            balance,
            this.transactionType!!,
            this.user!!,
        );
        transaction.updatedAt = updatedAt
        transaction.createAt = this.createAt
        transaction.id = this.id
        return transaction;
    }


}

enum class TransactionType {
    INCOME,
    EXPENSE
}