package io.siddhant.expense_tracker.controllers

import io.siddhant.expense_tracker.config.ApplicationResponse
import io.siddhant.expense_tracker.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(val userService: UserService) {

    @PostMapping("/signin")
    fun login(@RequestBody signInRequest: SignInRequest): ResponseEntity<Map<String, Any?>> {
        try {
            val user = userService.loginUser(signInRequest.username, signInRequest.password);
            return ApplicationResponse(
                message = "Success",
                status = HttpStatus.OK,
                data = userService.getUserWithProfile(user.id!!)
            ).build();
        } catch (e: Exception) {
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }
    }

    @PostMapping("/signup")
    fun signup(@RequestBody signUpRequest: SignUpRequest): ResponseEntity<Map<String, Any?>> {
        try {
            val user = userService.createUser(signUpRequest.username, signUpRequest.password, signUpRequest.name);
            return ApplicationResponse(message = "Success", status = HttpStatus.CREATED, data = user).build();
        } catch (e: Exception) {
            return ApplicationResponse(
                message = e.message ?: "Something went wrong",
                status = HttpStatus.BAD_REQUEST
            ).build();
        }
    }

    data class SignInRequest(val username: String, val password: String)
    data class SignUpRequest(val username: String, val password: String, val name: String)

}