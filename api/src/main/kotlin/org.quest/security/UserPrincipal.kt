package org.quest.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.quest.models.User
import java.util.*


class UserPrincipal : OAuth2User, UserDetails {

    private val id: Long?
    private val email: String
    private val password: String?
    private val authorities: Collection<GrantedAuthority>
    private var attributes: Map<String, Any>? = null

    constructor(id: Long?, email: String, password: String?, authorities: Collection<GrantedAuthority>) {
        this.id = id
        this.email = email
        this.password = password
        this.authorities = authorities
    }

    companion object {
        fun create(user: User): UserPrincipal {
            val authorities = Collections.singletonList(SimpleGrantedAuthority("ROLE_USER"))

            return UserPrincipal(
                    user.id,
                    user.email,
                    user.password,
                    authorities
            )
        }

        fun create(user: User, attributes: Map<String, Any>): UserPrincipal {
            val userPrincipal = UserPrincipal.create(user)
            userPrincipal.setAttributes(attributes)
            return userPrincipal
        }
    }


    fun getId(): Long? {
        return id
    }

    fun getEmail(): String {
        return email
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getAttributes(): Map<String, Any>? {
        return attributes
    }

    fun setAttributes(attributes: Map<String, Any>) {
        this.attributes = attributes
    }

    override fun getName(): String {
        return id.toString()
    }
}