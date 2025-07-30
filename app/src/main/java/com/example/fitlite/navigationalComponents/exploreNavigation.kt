import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitlite.components.ExerciseActivity
import com.example.fitlite.data.PersonViewModel
import com.example.fitlite.pages.ExplorePage

@Composable
fun ExploreNavigation(navController: NavHostController, modifier: Modifier, context: Context, tts: TextToSpeech) {
    NavHost(navController = navController, startDestination = "explorePage") {
        composable("explorePage") { ExplorePage(navController, ) }
        composable(
            route = "exerciseActivity/{temp}",
            arguments = listOf(
                navArgument("temp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dataId = backStackEntry.arguments?.getString("temp")
            ExerciseActivity(navController, Modifier.size(410.dp, 1000.dp), dataId!!, context, tts)
        }
    }
}