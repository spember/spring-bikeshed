package com.pember.bikeshed.http

import com.pember.bikeshed.auth.PrincipalAuthService
import com.pember.bikeshed.config.Constants
import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@CrossOrigin(origins = [Constants.CORS_ORIGINS])
@Controller
class UserDetailsController(private val authService: PrincipalAuthService) {

    @GetMapping("/user", consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun getCurrentUserDetails(): ResponseEntity<UserResponse.FoundUser> {
        val customer = authService.retrieveCurrentCustomer()
        val response = UserResponse.FoundUser(customer.id, customer.name)
        return ResponseEntity.ok(response)
    }
}