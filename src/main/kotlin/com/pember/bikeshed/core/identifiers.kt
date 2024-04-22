package com.pember.bikeshed.core

class identifiers {
}

data class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "User ID must not be blank" }
    }
}