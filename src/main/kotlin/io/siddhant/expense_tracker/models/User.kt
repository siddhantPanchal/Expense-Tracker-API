package io.siddhant.expense_tracker.models

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
open class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @Column(name = "username", nullable = false, unique = true, length = 45)
    open var username: String? = null
        protected set

    @Column(name = "password", nullable = false)
    @JsonIgnore
    open var password: String? = null


    @Column(name = "token", unique = true)
    open var token: String? = null

    constructor(username: String, password: String, token: String) {
        this.username = username.lowercase()
        this.password = password
        this.token = token
    }
}