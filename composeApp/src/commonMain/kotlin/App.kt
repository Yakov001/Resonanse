import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import data.Event
import data.Events
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.events.AddEventFab
import presentation.events.EventCard
import presentation.theme.ResonanseTheme

@[Composable OptIn(ExperimentalFoundationApi::class)]
fun App() {
    val scope = rememberCoroutineScope()
    ResonanseTheme {
        val events by remember { mutableStateOf(Events.startEvents.toMutableStateList()) }
        val listState = rememberLazyListState()
        val onFabClick : () -> Unit = {
            events.add(
                index = 0,
                element = Event.newRandom(events.size)
            )
            scope.launch {
                delay(100)
                listState.animateScrollToItem(0)
            }
        }
        Scaffold(
            floatingActionButton = { AddEventFab(onFabClick = onFabClick) }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(
                    items = events,
                    key = { item: Event -> item.title }
                ) {
                    EventCard(event = it, modifier = Modifier.animateItemPlacement())
                }
            }
        }
    }
}