package com.pember.bikeshed.http

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserOverviewService
import com.pember.bikeshed.core.users.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@CrossOrigin(origins = ["http://localhost:5173"])
@Controller
class UserDetailsController(private val userOverviewService: UserOverviewService) {

    @GetMapping("/user", consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun getCurrentUserDetails(): ResponseEntity<UserResponse.FoundUser> {
        return when (val response = userOverviewService.retrieveUser(UserId("foo123"))) {
            is UserResponse.FoundUser -> ResponseEntity.ok(response)
            is UserResponse.UserNotFound -> ResponseEntity.notFound().build()
        }
    }
}