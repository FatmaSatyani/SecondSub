package com.fatmasatyani.githubuser2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class Github (
        var name: String? = null,
        var username: String? = null,
        var location: String? = null,
        var repository: Int? = null,
        var company: String? = null,
        var followers: Int?= null,
        var following: Int? = null,
        var avatar: String? = null
    ) : Parcelable