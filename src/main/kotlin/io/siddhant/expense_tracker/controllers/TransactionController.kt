package io.siddhant.expense_tracker.controllers

import io.siddhant.expense_tracker.config.ApplicationResponse
import io.siddhant.expense_tracker.models.TransactionType
import io.siddhant.expense_tracker.models.User
import io.siddhant.expense_tracker.services.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/transactions")
class TransactionController(private val transactionService: TransactionService) {

    @PostMapping
    fun addTransaction(
        @RequestBody request: AddTransactionRequest,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Map<String, Any?>> {
        try {
            val transaction = transactionService.createTransaction(
                request.name,
                request.description,
                request.amount,
                request.transactionType,
                user
            );
            return ApplicationResponse(message = "Success", status = HttpStatus.CREATED, data = transaction).build();
        } catch (e: HttpMessageNotReadableException) {
            return ApplicationResponse(
                message = e.localizedMessage ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        } catch (e: Exception) {
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }

    }

    @GetMapping
    fun getAllTransactions(@RequestAttribute("user") user: User): ResponseEntity<Map<String, Any?>> {
        try {
            val transactions =
                transactionService.getAllTransactions(user, LocalDateTime.now().minusMonths(1), LocalDateTime.now());
            return ApplicationResponse(message = "Success", status = HttpStatus.OK, data = transactions).build();
        } catch (e: Exception) {
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTransaction(
        @PathVariable("id") id: Long,
        @RequestAttribute("user") user: User
    ): ResponseEntity<Map<String, Any?>> {
        try {
            transactionService.deleteTransaction(id, user);
            return ApplicationResponse(message = "Success", status = HttpStatus.OK).build();
        } catch (e: Exception) {
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }
    }

    @PutMapping("/{id}")
    fun updateTransaction(
        @PathVariable("id") id: Long,
        @RequestAttribute("user") user: User,
        @RequestBody request: UpdateTransactionRequest
    ): ResponseEntity<Map<String, Any?>> {
        try {
            if (request.name == null && request.amount == null && request.description == null) throw Exception("Invalid request");
            val updatedTransaction =
                transactionService.updateTransaction(id, user, request.name, request.description, request.amount);
            return ApplicationResponse(message = "Success", status = HttpStatus.OK, data = updatedTransaction).build();
        } catch (e: Exception) {
            e.printStackTrace()
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }

    }

    data class AddTransactionRequest(
        val name: String,
        val description: String?,
        val amount: Double,
        val transactionType: TransactionType
    )

    data class UpdateTransactionRequest(
        val name: String?,
        val description: String?,
        val amount: Double?,
    )


}