package com.mobdeve.s11.santos.andreali.everhourprototype

data class TimeEntry (
    val projectID: String = "",
    val timeEntryID: String = "",
    val name: String = "",
    val timeElapsed: String = "",
    val personInCharge: String = "",
    val billable: Boolean,
    val rate: Int = 0
)