package com.example.testapp.data.mapper

import com.example.testapp.data.local.models.ContributorLocalDto
import com.example.testapp.data.remote.model.ContributorRemoteDto

fun ContributorRemoteDto.toLocal() = ContributorLocalDto(
    id = id,
    login = login,
    node_id = node_id,
    avatar_url = avatar_url,
    gravatar_id = gravatar_id,
    url = url,
    html_url = html_url,
    followers_url = followers_url,
    following_url = following_url,
    gists_url = gists_url,
    starred_url = starred_url,
    subscriptions_url = subscriptions_url,
    organizations_url = organizations_url,
    repos_url = repos_url,
    events_url = events_url,
    received_events_url = received_events_url,
    type = type,
    site_admin = site_admin,
    contributions = contributions
)