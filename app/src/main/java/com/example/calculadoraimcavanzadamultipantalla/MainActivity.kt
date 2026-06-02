package com.example.calculadoraimcavanzadamultipantalla


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadoraimcavanzadamultipantalla.ui.theme.CalculadoraIMCAvanzadaMultipantallaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraIMCAvanzadaMultipantallaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Se inicia la navegación principal de la aplicación
                    AppNavegacion()
                }
            }
        }
    }

    @Composable
    fun AppNavegacion() {
        // Controlador encargado de gestionar la navegación entre pantallas
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "ingreso") {
            //Ruta 1: Pantalla inicial donde el usuario ingresa sus datos
            composable("ingreso") {
                PantallaIngreso(navController)
            }

            //Ruta 2: Pantalla que recibe el nombre y el IMC calculado
            composable("resultado/{nombre}/{imc}") { backStackEntry ->
                val nombre = backStackEntry.arguments?.getString("nombre") ?: ""
                val imc = backStackEntry.arguments?.getString("imc")?.toDoubleOrNull() ?: 0.0
                PantallaResultado(navController, nombre, imc)
            }
        }
    }


    @Composable
    fun PantallaIngreso(navController: NavController) {
        // Estados que almacenan la información ingresada por el usuario
        var nombre by remember { mutableStateOf("") }
        var peso by remember { mutableStateOf("") }
        var altura by remember { mutableStateOf("") }

        // Controla si se muestra el mensaje de error
        var error by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Calculadora de IMC", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            //Campo para ingresar el nombre
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Campo para ingresar el peso
            TextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso (kg)") },
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo para ingresar la altura
            TextField(
                value = altura,
                onValueChange = { altura = it },
                label = { Text("Altura (m)") },
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Muestra un mensaje cuando los datos no son válidos
            if (error) {
                Text(
                    text = "Por favor, completa todos los campos correctamente",
                    color = Color.Blue,
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    val pesoNumero = peso.toDoubleOrNull()
                    val alturaNumero = altura.toDoubleOrNull()

                    // Condición que verifica que peso y altura sean números válidos
                    if (
                        nombre.isBlank() ||
                        pesoNumero == null || pesoNumero <=0 ||
                        alturaNumero == null || alturaNumero <= 0

                    ) {
                        error = true

                    } else {
                        error = false

                        // Fórmula para calcular el IMC
                        val imc = pesoNumero / (alturaNumero * alturaNumero)

                        // Se envían el nombre y el IMC a la siguiente pantalla
                        navController.navigate("resultado/$nombre/$imc")
                    }
                }) {
                Text("Calcular IMC")
            }
        }
    }

    @Composable
    fun PantallaResultado(navController: NavController, nombre: String, imc: Double) {
        // Clasificación del IMC según el resultado obtenido
        val categoria = when {
            imc < 18.5 -> "Bajo peso"
            imc < 25 -> "Peso normal"
            imc < 30 -> "Sobrepeso"
            else -> "Obesidad"
        }

        // Define el color de la categoría según el resultado obtenido
        val colorCategoria = when {
            imc < 18.5 -> Color.Red //Rojo para bajo peso
            imc < 25 -> Color.Green //Verde para peso normal
            imc < 30 -> Color(0xFFFFA500) //Naranja para sobrepeso
            else -> Color(0xFF8E24AA) //Morado para obesidad
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Resultado", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // Mensaje que se muestra al usuario
            Text("Hola $nombre, tu resultado", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(15.dp))

            Text("IMC de ${String.format(Locale.US,"%.1f", imc)} tienes: ", fontSize = 26.sp, fontWeight = FontWeight.Bold)//número
            Spacer(modifier = Modifier.height(15.dp))

            Text(categoria, fontSize = 28.sp, color = colorCategoria, fontWeight = FontWeight.Bold) //categoria
            Spacer(modifier = Modifier.height(32.dp)) //30

            Button(
                onClick = {
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Volver a calcular")
            }
        }
    }

    //Preview
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun PreviewPantallaIngreso() {
        val navController = rememberNavController()
        PantallaIngreso(navController)
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun PreviewPantallaResultado() {
        val navController = rememberNavController()
        PantallaResultado(navController, "Pamela Torres", 22.5)
    }

    @Preview(showBackground = true, showSystemUi = true, name ="Bajo Peso")
    @Composable
    fun PreviewPantallaResultadoBajoPeso() {
        val navController = rememberNavController()
        PantallaResultado(navController, "Rene Lúcas", 17.2)
    }

    @Preview(showBackground = true, showSystemUi = true, name = "SobrePeso")
    @Composable
    fun PreviewPantallaResultadoSobrePeso() {
        val navController = rememberNavController()
        PantallaResultado(navController, "María Estrada", 28.9)
    }

    @Preview(showBackground = true, showSystemUi = true, name = "Obesidad")
    @Composable
    fun PreviewPantallaResultadoObesidad() {
        val navController = rememberNavController()
        PantallaResultado(navController, "Julio Tene", 34.1)
    }

}