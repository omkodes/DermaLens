package com.example.dermalens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,           // Always 1 — single active user
    val name: String,
    val age: Int,                           // Slider: 1–100
    val sex: String,                        // "Male" | "Female" | "Other"
    val skinType: String,                   // "Very Fair" | "Fair" | "Medium" | "Dark"
    val familyHistory: Boolean,             // +3 risk pts
    val previousSkinCancer: Boolean,        // +4 risk pts
    val hadBadSunburns: Boolean,            // +1 risk pt
    val consentGiven: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
