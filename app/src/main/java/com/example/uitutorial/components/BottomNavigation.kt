import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uitutorial.ui.theme.Purple40
import com.example.uitutorial.ui.theme.Purple80
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MyBottomAppBar(
    pagerState: PagerState,
    scope: CoroutineScope
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        NavigationBar(
            containerColor = Purple80,
            contentColor = Color.Transparent,
            modifier = Modifier
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                .width(350.dp)
        ) {
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = if (pagerState.currentPage == 0) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(8.dp), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                },
                label = { Text("Home") },
                selected = pagerState.currentPage == 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    selectedTextColor = Color.Transparent,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                alwaysShowLabel = false
            )

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = if (pagerState.currentPage == 1) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(8.dp), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                label = { Text("Profile") },
                selected = pagerState.currentPage == 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                alwaysShowLabel = false
            )

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = if (pagerState.currentPage == 2) Color.Black else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(8.dp), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Person",
                            tint = Color.White
                        )
                    }
                },
                label = { Text("Settings") },
                selected = pagerState.currentPage == 2,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                ),
                alwaysShowLabel = false
            )
        }
    }

}