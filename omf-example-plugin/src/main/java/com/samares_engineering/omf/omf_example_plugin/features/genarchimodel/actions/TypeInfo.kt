package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions

data class TypeInfo(
    val rawType: Class<*>?,
    val typeArguments: List<TypeInfo> = emptyList()
)
