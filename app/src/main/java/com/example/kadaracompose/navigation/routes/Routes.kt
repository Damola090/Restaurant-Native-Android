package com.example.kadaracompose.navigation.routes

import kotlinx.serialization.Serializable

// ── Top Level ─────────────────────────────────────────────────────────────────

@Serializable
object Home

// ── Restaurants ───────────────────────────────────────────────────────────────

// Separate route for the nested graph — MUST be different from startDestination
// This was the cause of the crash: graph and startDestination can't share a route
@Serializable
object RestaurantsGraph

@Serializable
object RestaurantsList

@Serializable
data class RestaurantDetail(val id: Int)

@Serializable
object RestaurantCreate

@Serializable
data class RestaurantUpdate(val id: Int)

// ── Sensors ───────────────────────────────────────────────────────────────────

@Serializable
object Sensors

// ── Networking ────────────────────────────────────────────────────────────────

@Serializable
object Posts

// ── Storage ───────────────────────────────────────────────────────────────────

@Serializable
object Notes

@Serializable
object Preferences