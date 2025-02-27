package io.siddhant.expense_tracker.repositories

import io.siddhant.expense_tracker.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsernameIgnoreCase(username: String): Optional<User>
    fun findByToken(token: String): Optional<User>
}