package com.example.alcoholtracker.domain.model

import com.example.alcoholtracker.R

enum class DrinkCategory(val id: Int, val nameString: String, val defaultABV: Double, val icon: Int) {
    BEER(1,"Beer", 6.0, R.drawable.beer),
    WINE(2,"Wine",12.0, R.drawable.wine),
    SPIRIT(3, "Spirit",39.0, R.drawable.glass_cup),
    COCKTAIL(4,"Cocktail", 10.0, R.drawable.glass_martini_alt),
    OTHER(5,"Other", 0.0, R.drawable.glass_martini_alt);
}

// Maps loose category labels (e.g. "Beer", "Cocktail") from Drink entries and remote sources to a DrinkCategory
fun drinkCategoryFromLabel(label: String): DrinkCategory =
    if (label.isBlank()) DrinkCategory.OTHER
    else DrinkCategory.entries.firstOrNull {
        it.name.equals(label, ignoreCase = true) ||
                it.nameString.equals(label, ignoreCase = true) ||
                it.nameString.startsWith(label, ignoreCase = true)
    } ?: DrinkCategory.OTHER
