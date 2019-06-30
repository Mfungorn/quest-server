package org.quest.controllers

import org.quest.exception.BadRequestException
import org.quest.models.AuthProvider
import org.quest.models.User
import org.quest.payload.AuthResponse
import org.quest.payload.LoginRequest
import org.quest.payload.SignUpRequest
import org.quest.repositories.UserRepository
import org.quest.security.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager


@RestController
@RequestMapping("/auth")
class AuthController {
    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    @Autowired
    private val tokenProvider: TokenProvider? = null

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager!!.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        )

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProvider!!.createToken(authentication)
        return ResponseEntity.ok<Any>(AuthResponse(token))
    }

    @PostMapping("/signup")
    fun registerUser(@Valid @RequestBody signUpRequest: SignUpRequest): ResponseEntity<*> {
        if (userRepository!!.existsByEmail(signUpRequest.getEmail()!!)!!) {
            throw BadRequestException("Email address already in use.")
        }

        // Creating user's account
        val user = User()
        user.name = signUpRequest.getName()!!
        user.email = signUpRequest.getEmail()!!
        user.password = signUpRequest.getPassword()
        user.provider = AuthProvider.local

        user.password = passwordEncoder!!.encode(user.password)

        val result = userRepository.save(user)

        val location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.id).toUri()

        return ResponseEntity.created(location)
                .body<Any>(ApiResponse(true, "User registered successfully@"))
    }
}