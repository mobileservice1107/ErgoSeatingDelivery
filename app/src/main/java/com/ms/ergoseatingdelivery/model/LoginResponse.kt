package com.ms.ergoseatingdelivery.model

data class LoginResponse(
    var id: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var avatarUrl: String,
    var type: String,
    var createAt: String,
    var updatedAt: String,
    var token: String)