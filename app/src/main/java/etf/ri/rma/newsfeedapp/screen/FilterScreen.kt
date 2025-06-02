package etf.ri.rma.newsfeedapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import etf.ri.rma.newsfeedapp.data.ChipData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(

        onDismissRequest = onDismiss,


        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Spasi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Otkaži")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun FilterScreen(
    sel_kategorije: Set<String>,
    RasponDatuma: Pair<Long, Long>?, NepozeljneRijeci: List<String>,
    onApply: (Set<String>, Pair<Long, Long>?, List<String>) -> Unit
) {
    var kategorije by remember { mutableStateOf(sel_kategorije.toSet()) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var pocetnoVrijeme by remember { mutableStateOf(RasponDatuma?.first) }
    var krajVrijeme by remember { mutableStateOf(RasponDatuma?.second) }
    var rijec by remember { mutableStateOf("") }
    var rijeci by remember { mutableStateOf(NepozeljneRijeci) }

    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val prikazaniDatum = if (pocetnoVrijeme != null && krajVrijeme != null) {
        "${sdf.format(Date(pocetnoVrijeme!!))};${sdf.format(Date(krajVrijeme!!))}"
    } else {
        "Odaberite raspon datuma"
    }

    val chipovi = listOf(

        ChipData("Sve",            "filter_chip_all", "Sve"),
        ChipData("Politika",       "filter_chip_pol", "Politika"),
        ChipData("Sport",          "filter_chip_spo", "Sport"),
        ChipData("Nauka",          "filter_chip_sci", "Nauka"),
        ChipData("Tehnologija",    "filter_chip_tech","Tehnologija"),
        ChipData("Posao",          "filter_chip_biz", "Posao"),
        ChipData("Zdravlje",       "filter_chip_health","Zdravlje"),
        ChipData("Zabava",         "filter_chip_ent", "Zabava"),
        ChipData("Hrana",          "filter_chip_food","Hrana"),
        ChipData("Putovanja",      "filter_chip_travel","Putovanja")
    )

    val bojachipa = if (isSystemInDarkTheme()) Color(0xFF312D2D) else Color(0xFF4C60B6)
    val bojapozadine = if (isSystemInDarkTheme()) Color(0xFF3E3838) else Color(0xFF9CAEEE)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bojapozadine
    ) {
        Column(modifier = Modifier.padding(13.dp)) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(chipovi) { chip ->
                    FilterChip(
                        selected = kategorije.contains(chip.kategorija),
                        onClick = { kategorije = setOf(chip.kategorija) },
                        label = { Text(chip.prikaz, color = if (isSystemInDarkTheme()) Color.White else Color.Black) },
                        modifier = Modifier.testTag(chip.tag),
                        leadingIcon = {
                            if (kategorije.contains(chip.kategorija)) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "selektovan",
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(containerColor = bojachipa)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = prikazaniDatum,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    modifier = Modifier.testTag("filter_daterange_display")
                )
                Spacer(modifier = Modifier.width(15.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color(0xFF1A1717) else Color(0xFF4C60B6),
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                    onClick = { showStartPicker = true },
                    modifier = Modifier.testTag("filter_daterange_button")
                ) {
                    Text("Odaberi raspon!")
                }

                if (showStartPicker) {
                    DatePickerModalInput(
                        onDateSelected = {
                            pocetnoVrijeme = it
                            showEndPicker = true
                        },
                        onDismiss = { showStartPicker = false }
                    )
                }
                if (showEndPicker) {
                    DatePickerModalInput(
                        onDateSelected = { krajVrijeme = it },
                        onDismiss = { showEndPicker = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = rijec,
                    onValueChange = { rijec = it },
                    label = { Text("Neželjena riječ") },
                    modifier = Modifier.testTag("filter_unwanted_input")
                )
                Spacer(modifier = Modifier.width(3.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) Color(0xFF1A1717) else Color(0xFF4C60B6),
                        contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    ),
                    onClick = {
                        val lcase = rijec.lowercase()
                        if (lcase.isNotBlank() && rijeci.all { it.lowercase() != lcase }) {
                            rijeci = rijeci + rijec
                            rijec = ""
                        }
                    },
                    modifier = Modifier.testTag("filter_unwanted_add_button")
                ) {
                    Text("Dodaj riječ")
                }
            }


            Column(
                modifier = Modifier
                    .testTag("filter_unwanted_list")
                    .verticalScroll(rememberScrollState())
            ) {
                rijeci.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .clickable { rijeci = rijeci.filter { r -> r != it } }
                            .testTag("filter_unwanted_list_item"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF1A1717) else Color(0xFF4C60B6),
                    contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                ),
                onClick = {
                    onApply(
                        if (kategorije.contains("Sve")) setOf("Sve") else kategorije,
                        if (pocetnoVrijeme != null && krajVrijeme != null) pocetnoVrijeme!! to krajVrijeme!! else null,
                        rijeci
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("filter_apply_button")
            ) {
                Text("Primijeni filtere")
            }
        }
    }
}




