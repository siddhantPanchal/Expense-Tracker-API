package io.siddhant.expense_tracker.services

import io.siddhant.expense_tracker.models.Profile
import io.siddhant.expense_tracker.models.User
import io.siddhant.expense_tracker.repositories.ProfileRepository
import io.siddhant.expense_tracker.repositories.UserRepository
import org.springframework.stereotype.Service
import java.lang.Exception
import java.util.UUID

@Service
class UserService(private val userRepository: UserRepository, private val profileRepository: ProfileRepository) {

    fun getUserByUsername(username: String): User? = userRepository.findByUsernameIgnoreCase(username).orElse(null)


    fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)


    fun getUserWithProfile(id: Long): Profile? {
        val user = getUserById(id) ?: return null
        return profileRepository.findByUser(user).orElse(null)
    }

    fun createUser(username: String, password: String, name: String): User {

        if (getUserByUsername(username) != null) throw Exception("User already exists")

        val token = UUID.randomUUID().toString()
        val user = User(username, password, token);
        userRepository.save(user);

        val profile = Profile(user, name);
        profileRepository.save(profile);

        return userRepository.save(user)
    }

    fun loginUser(username: String, password: String): User {
        val user = userRepository.findByUsernameIgnoreCase(username);
        if (user.isEmpty) throw Exception("User not found")
        if (user.get().password == password)
            return user.get()
        else
            throw Exception("Invalid password")
    }

    fun getUserByToken(token: String): User? {
        return userRepository.findByToken(token).orElse(null);
    }

}