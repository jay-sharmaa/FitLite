import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uitutorial.MainScreen
import com.example.uitutorial.viewModels.HomePageViewModel

@Composable
fun MyNav(homePageViewModel: HomePageViewModel, context : Context){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController) }
        composable("signup") { SignUpPage(navController) }
        composable("mainScreen") { MainScreen(homePageViewModel = homePageViewModel, context = context) }
    }
}
