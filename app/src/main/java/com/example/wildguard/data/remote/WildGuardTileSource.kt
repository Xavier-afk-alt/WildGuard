package com.example.wildguard.data.remote

import org.osmdroid.tileprovider.tilesource.XYTileSource

val WildGuardTileSource = XYTileSource(
    "WildGuardOpenStreetMap",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors"
)

// A separate cache namespace prevents old/corrupted Admin map tiles from
// being reused while keeping the same OpenStreetMap service as the User map.
val WildGuardAdminTileSource = XYTileSource(
    "WildGuardAdminOpenStreetMapV2",
    0,
    19,
    256,
    ".png",
    arrayOf("https://tile.openstreetmap.org/"),
    "© OpenStreetMap contributors"
)
