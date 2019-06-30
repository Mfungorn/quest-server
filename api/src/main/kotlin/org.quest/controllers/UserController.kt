package org.quest.controllers

import org.quest.exception.ResourceNotFoundException
import org.quest.models.User
import org.quest.repositories.UserRepository
import org.quest.security.CurrentUser
import org.quest.security.UserPrincipal
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.beans.factory.annotation.Autowired

@RestController
class UserController {

    @Autowired
    private val userRepository: UserRepository? = null

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): User {
        return userRepository!!.findById(userPrincipal.getId()!!)
                .orElseThrow<RuntimeException> { ResourceNotFoundException("User", "id", userPrincipal.getId()!!) }
    }
}