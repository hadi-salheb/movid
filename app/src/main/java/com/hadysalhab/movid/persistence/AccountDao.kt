package com.hadysalhab.movid.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hadysalhab.movid.account.AccountResponse

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountResponse(accountResponse: AccountResponse)

    @Query("SELECT * FROM account LIMIT 1")
    fun getAccount(): AccountResponse?

}
