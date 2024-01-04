package data

import androidx.compose.ui.graphics.vector.ImageVector

data class Event(
    val title : String,
    val description : String,
    private val iconIndex : Int
) {
    val icon : ImageVector
        get() = Events.eventIcons[iconIndex % Events.startEventsSize]
    companion object {
        fun newRandom(index : Int) : Event = Event(
            title = "Ивент №$index",
            description = "Это очень важный ивент. На него точно нужно пойти.",
            iconIndex = index
        )
    }
}
