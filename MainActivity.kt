package com.example.task9

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 4. VIEW MODEL
// Handles communication between Database and UI
class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    private val database = ShoppingDatabase.getDatabase(application)
    private val dao = database.shoppingDao()

    // Expose the list of items as a StateFlow (Auto-updates UI)
    val items = dao.getAllItems().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addItem(name: String, quantity: String, unit: String, price: String) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                name = name,
                quantity = quantity,
                unit = unit,
                price = price
            )
            dao.insertItem(newItem)
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            dao.deleteItem(item)
        }
    }
}

// 5. MAIN ACTIVITY
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShoppingListScreen()
                }
            }
        }
    }
}

// 6. UI COMPOSABLES
@Composable
fun ShoppingListScreen(viewModel: ShoppingViewModel = viewModel()) {
    // Collect the database items as state
    val shoppingList by viewModel.items.collectAsState()

    // Input States
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping List Database",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- INPUT FORM ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Price") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            viewModel.addItem(name, quantity, unit, price)
                            // Clear inputs
                            name = ""
                            quantity = ""
                            unit = ""
                            price = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Item")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLE HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Text("Name", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
            Text("Qty", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Price", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Del", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        }

        // --- SHOPPING LIST (TABLE) ---
        LazyColumn {
            items(shoppingList) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, modifier = Modifier.weight(2f))
                    Text("${item.quantity} ${item.unit}", modifier = Modifier.weight(1f))
                    Text(item.price, modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = { viewModel.deleteItem(item) },
                        modifier = Modifier.weight(0.5f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
                Divider()
            }
        }
    }
}