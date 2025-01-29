package com.example.uitutorial.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import com.example.uitutorial.R
import com.example.uitutorial.ui.theme.Purple40
import kotlinx.coroutines.delay
import java.lang.Float.max

@Composable
@Preview(showSystemUi = true)
fun ProfilePage() {
    var maxLoading : Float = 50f
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(true) }

    val square = remember{
        RoundedPolygon(
            4,
            rounding = CornerRounding(0.3f)
        )
    }
    val clipSquare = remember(square) {
        RoundedPolygonShape(polygon = square)
    }

    LaunchedEffect(Unit){
        loadProgress {progress ->
            currentProgress = progress
        }

        loading = false
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ){
        Box(
            modifier = Modifier
                .clip(clipSquare)
                .background(color = Purple40.copy(alpha = 0.95f))
                .width(420.dp)
                .height(350.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.White)
                    .padding(all = 5.dp)
                    .size(120.dp)
                    .clip(CircleShape)
            )
            CircularProgressIndicator(progress = currentProgress,
                modifier =  Modifier.size(130.dp),
                strokeWidth = 5.dp)
        }
        Box(
            modifier = Modifier
                .size(420.dp)
                .background(color = Color.Blue)
        ){

        }
        Box(
            modifier = Modifier
                .size(420.dp)
                .background(color = Color.Red)
        ){

        }
    }
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(3)
    }
}

fun RoundedPolygon.getBounds() = calculateBounds().let { Rect(it[0], it[1], it[2], it[3]) }
class RoundedPolygonShape(
    private val polygon: RoundedPolygon,
    private var matrix: Matrix = Matrix()
) : Shape {
    private var path = androidx.compose.ui.graphics.Path()
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        path.rewind()
        path = polygon.toPath().asComposePath()
        matrix.reset()
        val bounds = polygon.getBounds()
        val maxBounds = max(bounds.width, bounds.height)
        matrix.scale(size.width*1.3f/maxBounds, size.height/maxBounds)
        matrix.translate(-bounds.left/1.3f, -bounds.top/1.8f)
        matrix.rotateZ(45f)
        path.transform(matrix)
        return Outline.Generic(path)
    }
}