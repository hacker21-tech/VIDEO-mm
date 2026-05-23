package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCreatorScreen(
    viewModel: MainViewModel,
    innerPadding: PaddingValues
) {
    val coroutineScope = rememberCoroutineScope()
    var promptText by remember { mutableStateOf("") }
    var generateStatus by remember { mutableStateOf(0) } // 0:Idle, 1:Progress, 2:Done
    var selectedMusic by remember { mutableStateOf("Epic Cinematic") }
    
    val tracks by viewModel.audioTracks.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Video Generation",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "AI Text-to-Video Generator",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                "Generate videos with custom music directly via AI.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "AI VIDEO ENGINE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        placeholder = { Text("Describe the cinematic scene...") },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Music Inspiration", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(selectedMusic, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                        
                        Box {
                            IconButton(
                                onClick = { expanded = true },
                                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = "Music", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                tracks.forEach { track ->
                                    DropdownMenuItem(
                                        text = { Text("${track.title} - ${track.genre}") },
                                        onClick = {
                                            selectedMusic = track.title
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (promptText.isNotBlank()) {
                                coroutineScope.launch {
                                    generateStatus = 1
                                    val error = viewModel.generateVideoAndSave(promptText, selectedMusic)
                                    if (error == null || error.contains("fallback")) {
                                        generateStatus = 2
                                        promptText = error ?: ""
                                    } else {
                                        generateStatus = 0
                                        promptText = error // Show error in prompt field
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = generateStatus == 0 && promptText.isNotBlank()
                    ) {
                        Text("Generate Video", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Videocam, contentDescription = "Generate")
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(visible = generateStatus == 1) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI is generating your video...")
                }
            }
            
            AnimatedVisibility(visible = generateStatus == 2) {
                Text("Video rendering complete! Saved to Dashboard.", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
