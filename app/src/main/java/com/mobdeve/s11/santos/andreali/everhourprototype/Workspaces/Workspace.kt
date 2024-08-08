package com.mobdeve.s11.santos.andreali.everhourprototype

data class Workspace(
    val id: String = "",
    val name: String = "",
    val hours: Int = 0,
    var projectsCount: Int = 0, // Number of projects
    var memberCount: Int = 0 // Number of members, if needed
)
