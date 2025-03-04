import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.uitutorial.MainScreen
import com.example.uitutorial.data.PersonViewModel
import com.example.uitutorial.viewModels.HomePageViewModel

@Composable
fun MyNav(homePageViewModel: HomePageViewModel, context : Context, authViewModel: PersonViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController, authViewModel, context) }
        composable("signup") { SignUpPage(navController, authViewModel, context) }
        composable("mainScreen/{userName}") {
                backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName")
            if (userName != null) {
                MainScreen(homePageViewModel = homePageViewModel, context = context, authViewModel = authViewModel, userName = userName)
            }
        }
    }
}
