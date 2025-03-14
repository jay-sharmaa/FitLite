import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.uitutorial.data.Person
import com.example.uitutorial.data.PersonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController : NavController, authViewModel: PersonViewModel, context: Context, user: String) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Log.d("LoginPage", user.length.toString())
    Log.d("LoginPage", user)
    LaunchedEffect(Unit){
        if(user.isNotEmpty()){
            navController.navigate("mainScreen/${user}"){
                popUpTo("login"){inclusive = true}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "FitLite",
                    style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Sign in to your account",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    label = { Text("Email Address", color = Color.White) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    textStyle = TextStyle(color = Color.White),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    label = { Text("Password", color = Color.White) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        var personName: String? = null
                        var personPassword: String? = null
                        scope.launch {
                            personName = authViewModel.getPersonByName(emailState.value)
                                .map{ it?.name }
                                .firstOrNull()
                            personPassword = authViewModel.getPersonByName(emailState.value)
                                .map{ it?.password }
                                .firstOrNull()
                            if(personName != null){
                                if(personName == emailState.value && personPassword == passwordState.value){
                                    navController.navigate("mainScreen/${personName}"){
                                        popUpTo("login"){inclusive = true}
                                    }
                                }
                                else{
                                    Toast.makeText(context, "Wrong Email or Password", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                Toast.makeText(context, "User Does Not Exist", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("Sign In", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.clickable {

                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Don’t have an account? Sign up",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    modifier = Modifier.clickable {
                        navController.navigate(route = "signup")
                    }
                )
            }
        }
    }
}