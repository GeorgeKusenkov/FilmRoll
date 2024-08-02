package com.example.filmroll

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Paint
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColor
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.filmroll.ui.theme.FilmRollTheme
import com.example.filmroll.ui.theme.Gray
import com.example.filmroll.ui.theme.YellowStar
import com.example.filmsdata.DominantColors
import com.example.filmsdata.models.RandomFilm
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FilmRollTheme {
                val viewModel: FilmViewModel = hiltViewModel()
                val dominantColor by viewModel.dominantColor.collectAsState()


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
//                            .background(dominantColor)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Greeting(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(viewModel: FilmViewModel, modifier: Modifier) {
    val dominantColors by viewModel.dominantColors.collectAsState()
    val randomFilmState = viewModel.randomFilm.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.getRandomFilm()
    }

    AnimatedGradientBackground(
        colors = dominantColors,
        isLoading = randomFilmState.value is RandomFilmViewState.Loading,
        loadingDuration = 500,
        successDuration = 5000
    )

    when (val state = randomFilmState.value) {
        is RandomFilmViewState.Loading -> {
            Text(text = "Loading...")
        }
        is RandomFilmViewState.Error -> Text(modifier = modifier, text = "Error: ${state.message}")
        is RandomFilmViewState.Success -> {
            DraggableMovieCard(
                film = state.film,
                colors = dominantColors,
                onDismiss = { viewModel.loadNextRandomFilm() }
            )
        }
    }
}

@Composable
fun DraggableMovieCard(
    film: RandomFilm,
    colors: DominantColors,
    onDismiss: () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }

    val dismissThreshold = -300f
    val maxDragDistance = 600f  // Увеличили для более плавного изменения

    val animatedOffset by animateFloatAsState(
        targetValue = if (isDismissed) -1000f else offsetY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isDismissed) 0f else 1f - (abs(offsetY) / maxDragDistance).pow(0.7f),
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (offsetY < dismissThreshold && !isDismissed) {
                            isDismissed = true
                            onDismiss()
                        } else {
                            offsetY = 0f
                        }
                    }
                ) { change, dragAmount ->
                    change.consume()
                    val drag = dragAmount * 0.8f  // Увеличили чувствительность драга
                    offsetY = (offsetY + drag).coerceAtMost(0f)
                }
            }
    ) {
        MovieCard(
            film = film,
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(0, animatedOffset.roundToInt()) }
                .alpha(animatedAlpha),
            colors = colors
        )
    }
}

@Composable
fun AnimatedGradientBackground(
    colors: DominantColors,
    isLoading: Boolean,
    loadingDuration: Int,
    successDuration: Int
) {
    val color1 by animateColorAsState(
        targetValue = if (isLoading) Color(colors.lightVibrant) else Color(colors.lightMuted),
        animationSpec = tween(durationMillis = if (isLoading) loadingDuration else successDuration),
        label = ""
    )

    val color2 by animateColorAsState(
        targetValue = if (isLoading) Color(colors.vibrant) else Color(colors.muted),
        animationSpec = tween(durationMillis = if (isLoading) loadingDuration else successDuration),
        label = ""
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gradientBrush = Brush.linearGradient(
            colors = listOf(color1, color2),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
        drawRect(brush = gradientBrush)
    }
}

@Composable
fun MovieCard(film: RandomFilm, modifier: Modifier, colors: DominantColors) {
    Surface(
        modifier = modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(36.dp))
            .width(300.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(film.poster.url),
                contentDescription = film.name,
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .clip(RoundedCornerShape(36.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
//                Text(text = "${film.year}", fontSize = 12.sp, style = TextStyle(color = Color(colors.vibrant)))
                Text(text = "${film.year}", fontSize = 12.sp)
                Row {
                    Text(text = " ★ ", fontSize = 12.sp, color = YellowStar)
                    Text(text = "${film.rating}", fontSize = 12.sp)
                }

            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 0.dp),
                text = film.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                film.genres.take(3).forEach { genre ->
                    Tag(text = genre)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun Tag(text: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(0.5.dp, color = MaterialTheme.colorScheme.onBackground),
                RoundedCornerShape(16.dp)
            )
            .padding(vertical = 3.dp, horizontal = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            modifier = Modifier.padding(2.dp) // Дополнительный отступ, если необходимо
        )
    }
}

//@Preview(showBackground = true)
@Preview( uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun MovieCardPreview() {
    FilmRollTheme {

        val dominantColor = Color.Red

        Surface(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(26.dp))
                .width(200.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
//                horizontalAlignment = Alignment.Start,


            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Используйте реальный URL
                    contentDescription = "Один Дома",
                    modifier = Modifier
                        .height(250.dp)
                        .clip(RoundedCornerShape(26.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    Text(text = "2015", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Row {
                        Text(text = "★ ", fontSize = 12.sp, color = YellowStar)
                        Text(text = "8", fontSize = 12.sp)
                    }

                }
                Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 0.dp),
                        text = "Один Дома 111111",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Ужастик", "Комедия").forEach { genre ->
                        Tag(genre)
                    }
                }

            }
        }
    }
}