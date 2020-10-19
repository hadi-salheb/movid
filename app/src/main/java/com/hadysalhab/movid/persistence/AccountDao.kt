package com.hadysalhab.movid.persistence

import androidx.room.*
import com.hadysalhab.movid.account.AccountResponse

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountResponse(accountResponse: AccountResponse)

    @Delete
    fun deleteAccountResponse(accountResponse: AccountResponse)

    @Query("SELECT * FROM account LIMIT 1")
    fun getAccount(): AccountResponse?

}
