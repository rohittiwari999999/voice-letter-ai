package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TemplateItem
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.ExecutiveNavy
import com.example.ui.theme.PaperBackground
import com.example.ui.theme.PaperBorder
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.VoiceLetterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatesScreen(
    viewModel: VoiceLetterViewModel,
    modifier: Modifier = Modifier
) {
    val customTemplates by viewModel.customTemplates.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    val categories = TemplateItem.getCategories() + if (customTemplates.isNotEmpty()) listOf("My Custom") else emptyList()

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "All") {
            TemplateItem.ALL
        } else if (selectedCategory == "My Custom") {
            emptyList()
        } else {
            TemplateItem.ALL.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Letter Templates",
                        fontWeight = FontWeight.Bold,
                        color = ExecutiveNavy
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.HOME) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ExecutiveNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = PaperBackground,
        modifier = modifier.testTag("templates_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // If "My Custom" or "All" is selected, show user's custom templates
                if (selectedCategory == "My Custom" || (selectedCategory == "All" && customTemplates.isNotEmpty())) {
                    if (customTemplates.isNotEmpty()) {
                        item {
                            Text(
                                text = "My Custom Templates (${customTemplates.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ExecutiveNavy,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(customTemplates) { customLetter ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openExistingLetter(customLetter) }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = AccentGold.copy(alpha = 0.12f),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = null,
                                            tint = AccentGold,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = customLetter.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ExecutiveNavy
                                        )
                                        Text(
                                            text = "Created ${customLetter.dateString}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.openExistingLetter(customLetter) },
                                        colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue)
                                    ) {
                                        Text("Use")
                                    }
                                }
                            }
                        }
                    }
                }

                if (selectedCategory != "My Custom") {
                    item {
                        Text(
                            text = "Standard Letter Formats (${filteredTemplates.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ExecutiveNavy,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(filteredTemplates) { template ->
                        TemplateCard(
                            template = template,
                            onUseTemplate = { viewModel.loadTemplate(template) },
                            onVoiceGenerate = { viewModel.generateLetterFromVoice(template.promptSuggestion) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: TemplateItem,
    onUseTemplate: () -> Unit,
    onVoiceGenerate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ExecutiveBlue.copy(alpha = 0.08f),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = template.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ExecutiveBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = template.letterType.label,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = template.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ExecutiveNavy
            )

            Text(
                text = template.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Voice suggestion prompt
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = androidx.compose.foundation.BorderStroke(1.dp, PaperBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onVoiceGenerate)
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardVoice,
                        contentDescription = "Voice Prompt",
                        tint = AccentTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\"${template.promptSuggestion}\"",
                        fontSize = 12.sp,
                        color = ExecutiveNavy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onUseTemplate,
                    colors = ButtonDefaults.buttonColors(containerColor = ExecutiveBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Use Template")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
