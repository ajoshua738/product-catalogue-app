package com.example.productcatalogueapp.core

import java.util.Locale

// Helper functions to format prices
fun Double.asPrice(): String = String.format(Locale.US, "$%.2f", this)

fun Double.asRating(): String = String.format(Locale.US, "%.1f", this)