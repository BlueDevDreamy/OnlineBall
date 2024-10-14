package com.itsecurity.video.chat.agora.model

data class Component(
    val id: Int,
    val name: String,
    val subpages: List<Subpage>
)

val basicComponent = Component(
    0,
    "Basic",
    subpages = BasicSubpageLists
)

//val advanceComponent = Component(
//    1,
//    "Advance",
//    subpages = AdvanceSubpageLists
//)

val Components = listOf(
    basicComponent,
//    advanceComponent
)