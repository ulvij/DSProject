package com.example.testapp.data.local.models

import androidx.room.*

@Entity(tableName = "contributor_table")
data class ContributorLocalDto(
    @PrimaryKey val id: Long,
    val login: String,
    var node_id: String,
    var avatar_url: String,
    var gravatar_id: String,
    var url: String,
    var html_url: String,
    var followers_url: String,
    var following_url: String,
    var gists_url: String,
    var starred_url: String,
    var subscriptions_url: String,
    var organizations_url: String,
    var repos_url: String,
    var events_url: String,
    var received_events_url: String,
    var type: String,
    var site_admin: Boolean,
    var contributions: Int,
)