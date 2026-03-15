package com.ovk.budgetapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate

@Composable
fun BudgetScreen() {
    val context = LocalContext.current
    val vm = remember {
        ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as android.app.Application)
            .create(BudgetViewModel::class.java)
    }

    var accountName by remember { mutableStateOf("") }
    var accountBalance by remember { mutableStateOf("0") }

    var incomeAmount by remember { mutableStateOf("0") }
    var expenseAmount by remember { mutableStateOf("0") }
    var comment by remember { mutableStateOf("") }
    var forecastDate by remember { mutableStateOf(LocalDate.now().plusDays(30).toString()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Бюджет строительства", style = MaterialTheme.typography.headlineSmall)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Новый счет")
                OutlinedTextField(accountName, onValueChange = { accountName = it }, label = { Text("Название") })
                OutlinedTextField(accountBalance, onValueChange = { accountBalance = it }, label = { Text("Стартовый баланс") })
                Button(onClick = {
                    vm.addAccount(accountName, accountBalance.toDoubleOrNull() ?: 0.0)
                    accountName = ""
                    accountBalance = "0"
                }) { Text("Добавить счет") }
            }
        }

        val accounts = vm.accounts.value
        if (accounts.isNotEmpty()) {
            val firstAccount = accounts.first()
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Операции по счету: ${firstAccount.name}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(incomeAmount, onValueChange = { incomeAmount = it }, label = { Text("Доход") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(expenseAmount, onValueChange = { expenseAmount = it }, label = { Text("Расход") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(comment, onValueChange = { comment = it }, label = { Text("Комментарий") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.addIncome(firstAccount.id, incomeAmount.toDoubleOrNull() ?: 0.0, comment) }) { Text("Добавить доход") }
                        Button(onClick = {
                            val category = vm.categories.value.firstOrNull()
                            if (category != null) {
                                vm.addExpense(firstAccount.id, expenseAmount.toDoubleOrNull() ?: 0.0, category.id, 1L, comment)
                            }
                        }) { Text("Добавить расход") }
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Прогноз бюджета")
                OutlinedTextField(forecastDate, onValueChange = { forecastDate = it }, label = { Text("Дата YYYY-MM-DD") })
                Button(onClick = {
                    runCatching { LocalDate.parse(forecastDate) }.onSuccess { vm.calculateForecast(it) }
                }) { Text("Рассчитать") }
                vm.forecast.value?.let { Text("Ожидаемый баланс: %.2f".format(it)) }
            }
        }

        Text("Счета и балансы", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(accounts) { account ->
                Text("• ${account.name}: %.2f".format(account.balance))
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
