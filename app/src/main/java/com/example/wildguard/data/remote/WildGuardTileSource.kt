package com.example.wildguard.data.remote

import org.osmdroid.tileprovider.tilesource.XYTileSource

val WildGuardTileSource = XYTileSource(
    "CartoLight",
    0,
    20,
    256,
    ".png",
    arrayOf(
        "https://a.basemaps.cartocdn.com/light_all/",
        "https://b.basemaps.cartocdn.com/light_all/",
        "https://c.basemaps.cartocdn.com/light_all/"
    ),
    "© OpenStreetMap contributors © CARTO"
)
