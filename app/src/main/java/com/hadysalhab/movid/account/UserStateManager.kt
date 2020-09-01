package com.hadysalhab.movid.account

import com.google.gson.Gson


class UserStateManager(
    private var userState: UserState,
    private val gson: Gson
) {

    fun getSessionId() = userState.sessionID
    fun updateSessionId(sessionId: String) {
        userState = userState.copy(sessionID = sessionId)
    }

    //expose a copy of the global state, to avoid any client other than the state manager to change any props.
    fun getAccountResponse() = userState.accountResponse?.deepCopy(gson)

    /*
     * state = {
     * ...state,
     * accountResponse : {
     * ...action.payload
     *   }
     * }
     * */
    fun updateAccountResponse(accountResponse: AccountResponse?) {
        userState = userState.copy(accountResponse = accountResponse?.deepCopy(gson))
    }
}
