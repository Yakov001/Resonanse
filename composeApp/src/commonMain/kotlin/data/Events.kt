package data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List

object Events {
    const val startEventsSize = 3
    val startEvents : List<Event> = List(startEventsSize) {
        Event.newRandom(it)
    }
    val eventIcons = List(startEventsSize) {
        listOf(
            Icons.Outlined.FavoriteBorder,
            Icons.Outlined.Add,
            Icons.Outlined.List,
            Icons.Outlined.AccountBox,
            Icons.Outlined.Home,
        ).random()
    }
}