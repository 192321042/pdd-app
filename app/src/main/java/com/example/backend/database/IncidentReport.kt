package com.example.backend.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long,
    val type: String, // e.g. "Voice Stress", "Scream Detection", "Fall Detected", "Panic Button"
    val description: String,
    val liveLocation: String, // "Lat, Lng" or address string
    val voiceClipName: String?, // Filename for offline audio clip simulation
    val aiConfidenceScore: Int, // 0 to 100 percentage value
    val alertStatus: String, // "Sent", "Escalated", "Resolved"
    val user_email: String? = "",
    val user_name: String? = "",
    val user_mobile: String? = "",
    val alert_trigger_source: String? = "",
    val alerted_contact_names: String? = "",
    val alerted_contact_phones: String? = ""
)
