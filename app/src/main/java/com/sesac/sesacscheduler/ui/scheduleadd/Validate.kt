package com.sesac.sesacscheduler.ui.scheduleadd

object Validate {

    fun checkValidate(title: String): Boolean {
        if (validateTitle(title)) {
            return false
        }
        return true
    }
    private fun validateTitle(title: String): Boolean {
        return title.isNotEmpty()
    }

}