package io.siddhant.expense_tracker.models

import jakarta.persistence.*

@Entity
@Table(name = "profile")
open class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    open var user: User? = null

    @Column(name = "salary")
    open var salary: Double? = null

    @Column(name = "name", nullable = false)
    open var name: String? = null

    @Column(name = "profile_photo_path")
    open var profilePhotoPath: String? = null

    constructor(user: User, name: String) {
        this.user = user
        this.name = name
    }
}