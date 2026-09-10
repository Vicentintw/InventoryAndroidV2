package com.vr.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Product(val id:Int, val name:String, val category:String)
data class Warehouse(val id:Int, val name:String, val manager:String)
data class Stock(val productId:Int, val warehouseId:Int, var quantity:Int, val location:String)
data class Movement(val id:Int, val from:Int, val to:Int, val productId:Int, val quantity:Int, var status:String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InventoryApp() }
    }
}

@Composable
fun InventoryApp() {
    var logged by remember { mutableStateOf(false) }
    if (!logged) {
        LoginScreen { logged = true }
    } else {
        MainScreen()
    }
}

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("VR Inventory", style = MaterialTheme.typography.headlineLarge)
            Text("Control de bodegas e inventario", modifier = Modifier.padding(top=8.dp))
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(email, { email = it }, label={Text("Correo")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(password, { password = it }, label={Text("Contraseña")}, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onLogin,
                enabled = email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Iniciar sesión") }
            Spacer(Modifier.height(10.dp))
            Text("V1: cualquier correo y contraseña no vacíos funcionan.")
        }
    }
}

@Composable
fun MainScreen() {
    val warehouses = remember { mutableStateListOf(
        Warehouse(1,"Bodega Central","Administrador"),
        Warehouse(2,"Bodega Norte","Encargado")
    )}
    val products = remember { mutableStateListOf(
        Product(1,"Laptop","Tecnología"),
        Product(2,"Monitor","Tecnología"),
        Product(3,"Teclado","Accesorios")
    )}
    val stocks = remember { mutableStateListOf(
        Stock(1,1,15,"A-01"),
        Stock(2,1,20,"A-02"),
        Stock(3,2,30,"B-01")
    )}
    val movements = remember { mutableStateListOf(
        Movement(1,1,2,1,2,"Pendiente")
    )}
    var tab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title={ Text("VR Inventory V1") }) }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            NavigationBar {
                listOf("Inicio","Bodegas","Productos","Inventario","Movimientos").forEachIndexed { i, label ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text("${i+1}") },
                        label = { Text(label) }
                    )
                }
            }
            Box(Modifier.fillMaxSize().padding(16.dp)) {
                when(tab) {
                    0 -> Dashboard(warehouses, products, stocks, movements)
                    1 -> SimpleList("Bodegas", warehouses.map { "${it.name} — ${it.manager}" })
                    2 -> SimpleList("Productos", products.map { "${it.name} — ${it.category}" })
                    3 -> InventoryList(stocks, products, warehouses)
                    4 -> MovementsList(movements, products, warehouses, stocks)
                }
            }
        }
    }
}

@Composable
fun Dashboard(
    warehouses: List<Warehouse>,
    products: List<Product>,
    stocks: List<Stock>,
    movements: List<Movement>
) {
    Column {
        Text("Panel principal", style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth().padding(bottom=10.dp)) {
            Column(Modifier.padding(18.dp)) {
                Text("Bodegas: ${warehouses.size}")
                Text("Productos: ${products.size}")
                Text("Unidades registradas: ${stocks.sumOf { it.quantity }}")
                Text("Movimientos pendientes: ${movements.count { it.status == "Pendiente" }}")
            }
        }
        Text("Bienvenido a la V1 de VR Inventory.")
    }
}

@Composable
fun SimpleList(title:String, rows:List<String>) {
    Column {
        Text(title, style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            items(rows) { row ->
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp)) {
                    Text(row, Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun InventoryList(
    stocks: List<Stock>,
    products: List<Product>,
    warehouses: List<Warehouse>
) {
    Column {
        Text("Inventario", style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            items(stocks) { s ->
                val p = products.firstOrNull { it.id == s.productId }?.name ?: "Producto"
                val w = warehouses.firstOrNull { it.id == s.warehouseId }?.name ?: "Bodega"
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(p, style=MaterialTheme.typography.titleMedium)
                        Text("$w • ${s.quantity} unidades • Ubicación ${s.location}")
                    }
                }
            }
        }
    }
}

@Composable
fun MovementsList(
    movements: MutableList<Movement>,
    products: List<Product>,
    warehouses: List<Warehouse>,
    stocks: MutableList<Stock>
) {
    Column {
        Text("Solicitudes de movimiento", style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(10.dp))
        LazyColumn {
            items(movements, key={it.id}) { m ->
                val p = products.firstOrNull { it.id == m.productId }?.name ?: "Producto"
                val from = warehouses.firstOrNull { it.id == m.from }?.name ?: "Origen"
                val to = warehouses.firstOrNull { it.id == m.to }?.name ?: "Destino"
                Card(Modifier.fillMaxWidth().padding(vertical=5.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("$p — ${m.quantity} unidades", style=MaterialTheme.typography.titleMedium)
                        Text("$from → $to")
                        Text("Estado: ${m.status}")
                        if (m.status == "Pendiente") {
                            Row(Modifier.padding(top=10.dp)) {
                                Button(onClick={
                                    val origin = stocks.firstOrNull { it.productId==m.productId && it.warehouseId==m.from }
                                    val destination = stocks.firstOrNull { it.productId==m.productId && it.warehouseId==m.to }
                                    if (origin != null && origin.quantity >= m.quantity) {
                                        origin.quantity -= m.quantity
                                        if (destination != null) destination.quantity += m.quantity
                                        else stocks.add(Stock(m.productId,m.to,m.quantity,"NUEVA"))
                                        m.status = "Aprobado"
                                    } else {
                                        m.status = "Rechazado"
                                    }
                                }) { Text("Aprobar") }
                                Spacer(Modifier.width(8.dp))
                                OutlinedButton(onClick={ m.status="Rechazado" }) { Text("Rechazar") }
                            }
                        }
                    }
                }
            }
        }
    }
}
