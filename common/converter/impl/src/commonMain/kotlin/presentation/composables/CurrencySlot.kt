package presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.request.crossfade

@Composable
fun CurrencySlot(
    flagImageUrl: String,
    currencyName: String,
    currencyCode: String,
    onClick: () -> Unit,
    textField: @Composable () -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.Start
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(shape)
            .clickable { onClick() }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape
            )
            .padding(16.dp)
    ) {
        SubcomposeAsyncImage(
            model = flagImageUrl,
            contentDescription = null,
            imageLoader = ImageLoader.Builder(LocalPlatformContext.current)
                .crossfade(true)
                .build(),
            loading = { CircularProgressIndicator() },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = currencyName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        textField()
    }
}

@Composable
fun CurrencySlotTextView(
    text: String,
    onTextChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        onValueChange = {
            onTextChange(it)
        },
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    )
}