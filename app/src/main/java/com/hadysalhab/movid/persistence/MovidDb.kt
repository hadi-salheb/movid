package com.hadysalhab.movid.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hadysalhab.movid.account.AccountResponse

@Database(
    entities = [AccountResponse::class],
    version = 1,
    exportSchema = false
)
abstract class MovidDB : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao

    companion object {
        val DATABASE_NAME: String = "movid_db"
    }
}