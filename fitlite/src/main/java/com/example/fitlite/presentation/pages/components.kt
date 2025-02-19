package com.example.fitlite.presentation.pages

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedModifier
import androidx.wear.compose.foundation.CurvedScope
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedComposable
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.foundation.padding
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.TimeTextDefaults
import androidx.wear.compose.material.TimeTextDefaults.CurvedTextSeparator
import com.example.fitlite.R
import com.example.fitlite.presentation.ViewModel.BackgroundViewModel

@Composable
fun HomeLayout(
    context: Context,
    backgroundViewModel: BackgroundViewModel,
    modifier: Modifier = Modifier,
    timeTextStyle: TextStyle = TimeTextDefaults.timeTextStyle(),
    startCurvedContent: (CurvedScope.() -> Unit)? = null,
    endCurvedContent: (CurvedScope.() -> Unit)? = null,
    textCurvedSeparator: CurvedScope.() -> Unit = {
        CurvedTextSeparator(curvedTextStyle = CurvedTextStyle(timeTextStyle))
    },
    navController: NavController
) {
    CurvedLayout(
        modifier = modifier,
        anchor = 0f
    ) {
        curvedRow(modifier = CurvedModifier.padding(0.dp)) {
            startCurvedContent?.let {
                it.invoke(this)
                textCurvedSeparator()
            }

            curvedComposable {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Heart",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-5f)
                        .clickable {
                            navController.navigate(route = "heartRate")
                        }
                )
            }

            curvedComposable {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "exercise",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-30f)
                        .clickable {
                            navController.navigate(route = "exercise")
                        }
                )
            }

            curvedComposable {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "music",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-70f)
                        .clickable {
                            navController.navigate(route = "music")
                        }
                )
            }

            // Custom XML Drawables (Force Upright with Rotation)
            curvedComposable {
                Icon(
                    painter = painterResource(id = R.drawable.run_fitlite),
                    contentDescription = "run",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-90f)
                        .clickable {
                            navController.navigate(route = "run")
                        }
                )
            }

            curvedComposable {
                Icon(
                    painter = painterResource(id = R.drawable.walf_fitlite),
                    contentDescription = "walk",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-135f)
                        .clickable {
                            navController.navigate(route = "walk")
                        }
                )
            }

            curvedComposable {
                Icon(
                    painter = painterResource(id = R.drawable.spo_fitlite),
                    contentDescription = "spo2",
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(-180f)
                        .clickable {
                            navController.navigate(route = "oxygen")
                        }
                )
            }

            endCurvedContent?.let {
                textCurvedSeparator()
                it.invoke(this)
            }
        }
    }
}
