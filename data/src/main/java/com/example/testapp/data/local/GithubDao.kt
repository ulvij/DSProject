package com.example.testapp.data.local

import androidx.room.*
import com.example.testapp.data.local.models.ContributorLocalDto
import kotlinx.coroutines.flow.Flow

@Dao
interface GithubDao {

    @Query("SELECT * from contributor_table")
    fun observeContributors(): Flow<List<ContributorLocalDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributors(list: List<ContributorLocalDto>)

    @Query("DELETE FROM contributor_table")
    fun clearData()

}