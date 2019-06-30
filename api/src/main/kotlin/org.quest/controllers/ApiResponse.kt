package org.quest.controllers

class ApiResponse {

    private var success: Boolean = false
    private var message: String? = null

    constructor(success: Boolean, message: String) {
        this.success = success
        this.message = message
    }

    fun isSuccess(): Boolean {
        return success
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }
}