import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.songlist.viewmodel.SongListEvent

@Composable
fun LoadingItem(onEvent: (SongListEvent) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator(
            modifier = Modifier
                .height(40.dp),
        )
    }
    LaunchedEffect(Unit) {
        onEvent(SongListEvent.OnLoadMore)
    }
}