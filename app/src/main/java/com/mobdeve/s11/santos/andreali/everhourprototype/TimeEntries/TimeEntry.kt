package com.mobdeve.s11.santos.andreali.everhourprototype

data class TimeEntry(
    val workplaceID: String = "",
    val projectID: String = "",
    val timeEntryID: String = "",
    val name: String = "",
    val timeElapsed: String = "00:00:00", // Default value set to "00:00:00"
    val personInCharge: String = "",
    val billable: Boolean = false, // Default value needed
    val rate: Int = 0
)
