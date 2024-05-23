package com.pember.bikeshed.http

import com.pember.bikeshed.core.UserId
import com.pember.bikeshed.core.users.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@CrossOrigin(origins = ["http://localhost:5173"])
@Controller
class UserDetailsController() {

    @GetMapping("/user", consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun getCurrentUserDetails(): ResponseEntity<UserResponse.FoundUser> {
        val response = UserResponse.FoundUser(UserId("foo"), "Bob")
        return ResponseEntity.ok(response)
    }
}