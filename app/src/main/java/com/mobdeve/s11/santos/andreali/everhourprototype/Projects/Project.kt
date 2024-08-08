package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Parcel
import android.os.Parcelable

data class Project(
    val name: String = "",
    val client: String = "",
    val roleIC: String = "",
    val workspaceId: String = "",
    val projectID: String = ""
)
