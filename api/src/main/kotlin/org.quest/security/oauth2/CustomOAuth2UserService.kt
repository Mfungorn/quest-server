package org.quest.security.oauth2

import org.springframework.stereotype.Service
import org.quest.security.oauth2.user.OAuth2UserInfo
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.quest.security.oauth2.user.OAuth2UserInfoFactory
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.quest.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;
import org.quest.exception.OAuth2AuthenticationProcessingException
import org.quest.models.AuthProvider
import org.quest.models.User
import org.quest.security.UserPrincipal

@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {

    @Autowired
    private val userRepository: UserRepository? = null

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }

    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.clientRegistration.registrationId, oAuth2User.attributes)
        if (StringUtils.isEmpty(oAuth2UserInfo!!.getEmail())) {

            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }

        val userOptional = userRepository!!.findByEmail(oAuth2UserInfo!!.getEmail())
        var user: User
        if (userOptional.isPresent) {
            user = userOptional.get()
            if (!user.provider.equals(AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId))) {
                throw OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.provider + " account. Please use your " + user.provider +
                        " account to login.")
            }
            user = updateExistingUser(user, oAuth2UserInfo!!)
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo!!)
        }

        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User()
        user.provider = AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        user.providerId = oAuth2UserInfo.getId()
        user.name = oAuth2UserInfo.getName()
        user.email = oAuth2UserInfo.getEmail()
        return userRepository!!.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        existingUser.name = oAuth2UserInfo.getName()
        return userRepository!!.save<User>(existingUser)
    }
}