package io.siddhant.expense_tracker.repositories

import io.siddhant.expense_tracker.models.Profile
import io.siddhant.expense_tracker.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProfileRepository : JpaRepository<Profile, Long> {
    fun findByUser(user: User): Optional<Profile>
}