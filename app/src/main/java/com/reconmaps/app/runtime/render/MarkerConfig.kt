package com.reconmaps.app.runtime.render

import android.graphics.Color

// 🔹 Marker Roles (Authoritative List)
enum class MarkerRole {
    LEADER,
    SWEEP,
    VEHICLE,
    SELF,
    PHOTO_WAYPOINT,
    MANUAL_WAYPOINT,
    BREADCRUMB,
    ECHO_11,
    UNKNOWN
}

object MarkerConfig {

    // 🔹 Shape Definitions
    enum class Shape {
        CIRCLE,
        TRIANGLE,
        SQUARE,
        DIAMOND,
        CROSS,
        DOT
    }

    // 🔹 Marker Style Definition
    data class MarkerStyle(
        val priority: Int,
        val color: Int,
        val shape: Shape,
        val hasHalo: Boolean = false
    )

    // 🔹 Central Style Map
    val styles: Map<MarkerRole, MarkerStyle> = mapOf(

        // 🟢 Priority 1 — Leader
        MarkerRole.LEADER to MarkerStyle(
            priority = 1,
            color = Color.GREEN,
            shape = Shape.TRIANGLE
        ),

        // 🟠 Priority 2 — Sweep
        MarkerRole.SWEEP to MarkerStyle(
            priority = 2,
            color = Color.rgb(255, 165, 0), // Orange
            shape = Shape.SQUARE
        ),

        // 🔵 Priority 3 — Convoy Vehicle
        MarkerRole.VEHICLE to MarkerStyle(
            priority = 3,
            color = Color.RED,
            shape = Shape.CIRCLE
        ),

        // 🔵 Priority 4 — Self (Halo handled in overlay)
        MarkerRole.SELF to MarkerStyle(
            priority = 4,
            color = Color.BLUE,
            shape = Shape.CIRCLE,
            hasHalo = true
        ),

        // 🟡 Priority 5 — Photo Waypoint
        MarkerRole.PHOTO_WAYPOINT to MarkerStyle(
            priority = 5,
            color = Color.YELLOW,
            shape = Shape.DIAMOND
        ),

        // 🔴 Priority 6 — Manual Waypoint
        MarkerRole.MANUAL_WAYPOINT to MarkerStyle(
            priority = 6,
            color = Color.RED,
            shape = Shape.CROSS
        ),

        // ⚪ Priority 7 — Breadcrumb
        MarkerRole.BREADCRUMB to MarkerStyle(
            priority = 7,
            color = Color.LTGRAY,
            shape = Shape.DOT
        ),

        // 🟣 Priority 0 — Echo 11 (Top priority placeholder)
        MarkerRole.ECHO_11 to MarkerStyle(
            priority = 0,
            color = Color.MAGENTA,
            shape = Shape.DIAMOND
        ),

        // ⚫ Fallback
        MarkerRole.UNKNOWN to MarkerStyle(
            priority = 999,
            color = Color.DKGRAY,
            shape = Shape.CIRCLE
        )
    )

    // 🔹 Safe Access Helper
    fun getStyle(role: MarkerRole): MarkerStyle {
        return styles[role] ?: styles[MarkerRole.UNKNOWN]!!
    }
}