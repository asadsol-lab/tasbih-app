package com.example.ui.zikr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditZikrScreen(
    zikrId: Long?,
    viewModel: ZikrViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var arabicText by remember { mutableStateOf("") }
    var urduTranslation by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("33") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(zikrId) {
        if (zikrId != null && zikrId > 0) {
            val zikr = viewModel.getZikrById(zikrId)
            if (zikr != null) {
                name = zikr.name
                arabicText = zikr.arabicText
                urduTranslation = zikr.urduTranslation
                targetText = zikr.defaultTarget?.toString() ?: "33"
            }
        }
    }

    Scaffold(
        modifier = modifier.testTag("add_edit_zikr_screen"),
        topBar = {
            TopAppBar(
                title = { Text(if (zikrId != null && zikrId > 0) "Edit Custom Dhikr" else "Add Custom Dhikr") },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Friendly note
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "Custom entries are personal to your device and are clearly labeled as user-created dhikr.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    validationError = null
                },
                label = { Text("Dhikr Name *") },
                placeholder = { Text("e.g., Rabbi Zidni Ilma") },
                isError = validationError != null && name.isBlank(),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_zikr_name")
            )

            // Arabic Text
            OutlinedTextField(
                value = arabicText,
                onValueChange = { arabicText = it },
                label = { Text("Arabic Text (Optional)") },
                placeholder = { Text("رَبِّ زِدْنِي عِلْمًا") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_arabic_text")
            )

            // Urdu Translation
            OutlinedTextField(
                value = urduTranslation,
                onValueChange = { urduTranslation = it },
                label = { Text("Urdu Translation / Meaning (Optional)") },
                placeholder = { Text("اے میرے رب! میرے علم میں اضافہ فرما") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_urdu_translation")
            )

            // Default Target
            OutlinedTextField(
                value = targetText,
                onValueChange = { targetText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Default Target (Optional)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_default_target")
            )

            if (validationError != null || uiState.errorMessage != null) {
                Text(
                    text = validationError ?: uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_save_zikr_button")
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            validationError = "Please enter a name for the dhikr"
                        } else {
                            val target = targetText.toIntOrNull()
                            viewModel.saveZikr(
                                id = zikrId,
                                name = name,
                                arabicText = arabicText,
                                urduTranslation = urduTranslation,
                                target = target,
                                onSuccess = onNavigateBack
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("save_zikr_button")
                ) {
                    Text("Save Dhikr")
                }
            }
        }
    }
}
