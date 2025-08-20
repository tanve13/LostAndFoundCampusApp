package com.tanveer.lostandcampusapp.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {
    var name = mutableStateOf("")
    var regNo = mutableStateOf("")

    fun setUserData(userName: String, userReg: String) {
        name.value = userName
        regNo.value = userReg
    }
}