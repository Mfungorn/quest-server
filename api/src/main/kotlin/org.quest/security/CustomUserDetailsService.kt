package org.quest.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.quest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.quest.exception.ResourceNotFoundException


@Service
class CustomUserDetailsService: UserDetailsService {
    @Autowired
    var userRepository: UserRepository? = null

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository!!.findByEmail(email)
                .orElseThrow { UsernameNotFoundException("User not found with email : $email") }

        return UserPrincipal.create(user)
    }

    @Transactional
    fun loadUserById(id: Long?): UserDetails {
        val user = userRepository!!.findById(id).orElseThrow<RuntimeException> { ResourceNotFoundException("User", "id", id as Any) }

        return UserPrincipal.create(user)
    }
}