package com.pember.bikeshed.http

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.function.ServerResponse

@Controller
class SampleController {

    @GetMapping("/sample", produces = ["application/json"])
    @ResponseBody
    fun getSample(): String {
        return "Hello, World!"
    }


}