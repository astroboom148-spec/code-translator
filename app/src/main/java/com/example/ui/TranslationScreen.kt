package com.example.ui

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TranslationEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationScreen(
    viewModel: TranslationViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val sourceLanguage by viewModel.sourceLanguage.collectAsState()
    val targetLanguage by viewModel.targetLanguage.collectAsState()
    val sourceCode by viewModel.sourceCode.collectAsState()
    val translatedCode by viewModel.translatedCode.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val addComments by viewModel.addComments.collectAsState()
    val addExplanation by viewModel.addExplanation.collectAsState()
    val translationHistory by viewModel.translationHistory.collectAsState()

    var isSourceMenuExpanded by remember { mutableStateOf(false) }
    var isTargetMenuExpanded by remember { mutableStateOf(false) }
    var isHistoryExpanded by remember { mutableStateOf(false) }

    val languages = listOf("Auto-detect", "Kotlin", "Java", "Python", "Go", "TypeScript", "JavaScript", "Rust", "C++", "C#", "Ruby", "Swift", "PHP", "HTML")
    val targetLanguages = languages.filter { it != "Auto-detect" }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Identity Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
            Text(
                text = "⟨ ⟩ ",
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "Code Translator",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Language Selection Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Source Language dropdown selector
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { isSourceMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("source_lang_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = sourceLanguage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select source language",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = isSourceMenuExpanded,
                        onDismissRequest = { isSourceMenuExpanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    viewModel.setSourceLanguage(lang)
                                    isSourceMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Swap Languages button
                IconButton(
                    onClick = { viewModel.swapLanguages() },
                    enabled = sourceLanguage != "Auto-detect",
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(48.dp)
                        .testTag("swap_lang_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Swap Languages",
                        tint = if (sourceLanguage != "Auto-detect") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                // Target Language dropdown selector
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { isTargetMenuExpanded = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("target_lang_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = targetLanguage,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select target language",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = isTargetMenuExpanded,
                        onDismissRequest = { isTargetMenuExpanded = false }
                    ) {
                        targetLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    viewModel.setTargetLanguage(lang)
                                    isTargetMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Source Code Input Field
        OutlinedTextField(
            value = sourceCode,
            onValueChange = { viewModel.setSourceCode(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .testTag("source_code_input"),
            placeholder = {
                Text(
                    text = "// Paste or type your $sourceLanguage code here...",
                    style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                )
            },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
            trailingIcon = {
                if (sourceCode.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearInput() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear code"
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Options toggles (Comments & Explanation)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.setAddComments(!addComments) }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Switch(
                        checked = addComments,
                        onCheckedChange = { viewModel.setAddComments(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add comments",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.setAddExplanation(!addExplanation) }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Switch(
                        checked = addExplanation,
                        onCheckedChange = { viewModel.setAddExplanation(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add explanation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Translate Button
        Button(
            onClick = { viewModel.translate() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("translate_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Translating Code...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Translate",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Translate to $targetLanguage",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Error message display
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Translation Result Area
        AnimatedVisibility(
            visible = translatedCode.isNotEmpty() && !isLoading,
            enter = fadeIn(animationSpec = tween(500)),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                // Translated Title and Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TRANSLATED CODE ($targetLanguage)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row {
                        IconButton(
                            onClick = {
                                val cleanText = cleanMarkdownCode(translatedCode)
                                clipboardManager.setText(AnnotatedString(cleanText))
                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Code",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = {
                                shareText(context, cleanMarkdownCode(translatedCode), targetLanguage)
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Code",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Terminal block
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1E1E))
                        .padding(16.dp),
                    color = Color(0xFF1E1E1E)
                ) {
                    Text(
                        text = highlightCode(translatedCode),
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History expansion header
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isHistoryExpanded = !isHistoryExpanded },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Recent Translations (${translationHistory.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isHistoryExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isHistoryExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Expansible list of past items
        AnimatedVisibility(
            visible = isHistoryExpanded,
            enter = fadeIn() + fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                if (translationHistory.isEmpty()) {
                    Text(
                        text = "No translation records yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Clear History",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .clickable { viewModel.clearHistory() }
                                .padding(8.dp)
                        )
                    }

                    translationHistory.forEach { item ->
                        HistoryItemCard(
                            item = item,
                            onLoad = {
                                viewModel.loadFromHistory(item)
                                isHistoryExpanded = false
                            },
                            onDelete = { viewModel.deleteHistoryItem(item.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        }

        // Small, clean footer credits at the absolute bottom-left corner
        Text(
            text = "Made by Abd el Nasser Elkhattabi",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 12.dp)
        )
    }
}

@Composable
fun HistoryItemCard(
    item: TranslationEntity,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLoad() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.sourceLanguage,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = " ➔ ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = item.targetLanguage,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete record",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Short preview of source code
            Text(
                text = item.sourceCode.trim().take(120) + if (item.sourceCode.length > 120) "..." else "",
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, lineHeight = 14.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}



private fun shareText(context: Context, text: String, language: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_TITLE, "Code translated to $language")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share translated code")
    context.startActivity(shareIntent)
}

// Clean markdown blocks
private fun cleanMarkdownCode(raw: String): String {
    var output = raw.trim()
    if (output.startsWith("```")) {
        // Strip the first opening code block line
        val firstLineEnd = output.indexOf("\n")
        if (firstLineEnd != -1) {
            output = output.substring(firstLineEnd + 1)
        }
    }
    if (output.endsWith("```")) {
        output = output.substring(0, output.length - 3).trim()
    }
    return output
}

// Professional syntax highlighting simulator for Jetpack Compose
fun highlightCode(rawCode: String): AnnotatedString {
    val cleanCode = cleanMarkdownCode(rawCode)
    
    // Core IDE syntax highlighting colors
    val keywordColor = Color(0xFFE5C07B) // Warm golden
    val functionColor = Color(0xFF61AFEF) // Ocean blue
    val stringColor = Color(0xFF98C379) // Calm forest green
    val commentColor = Color(0xFF7F848E) // Muted slate gray
    val numberColor = Color(0xFFD19A66) // Soft orange
    val normalColor = Color(0xFFABB2BF) // Standard gray-white
    val typeColor = Color(0xFF4ECEC9) // Teal

    val keywords = setOf(
        "val", "var", "fun", "function", "def", "class", "return", "if", "else", 
        "while", "for", "import", "package", "struct", "interface", "public", 
        "private", "protected", "fn", "let", "const", "nil", "null", "true", 
        "false", "try", "catch", "throw", "break", "continue", "in", "as", "is"
    )

    val keyTypes = setOf(
        "String", "Int", "Double", "Float", "Boolean", "Long", "Short", "Byte",
        "Char", "void", "any", "number", "string", "boolean", "List", "Map", "Set"
    )

    return buildAnnotatedString {
        var index = 0
        val length = cleanCode.length

        while (index < length) {
            val char = cleanCode[index]

            // Comment matching helper
            if (char == '/' && index + 1 < length && cleanCode[index + 1] == '/') {
                val endOfLine = cleanCode.indexOf('\n', index)
                val commentEnd = if (endOfLine == -1) length else endOfLine
                pushStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                append(cleanCode.substring(index, commentEnd))
                pop()
                index = commentEnd
                continue
            }
            if (char == '#') {
                val endOfLine = cleanCode.indexOf('\n', index)
                val commentEnd = if (endOfLine == -1) length else endOfLine
                pushStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                append(cleanCode.substring(index, commentEnd))
                pop()
                index = commentEnd
                continue
            }

            // String literals
            if (char == '"' || char == '\'') {
                val quote = char
                val stringStart = index
                index++
                var escaped = false
                while (index < length) {
                    val current = cleanCode[index]
                    if (escaped) {
                        escaped = false
                    } else if (current == '\\') {
                        escaped = true
                    } else if (current == quote) {
                        break
                    }
                    index++
                }
                val stringEnd = if (index < length) index + 1 else length
                pushStyle(SpanStyle(color = stringColor))
                append(cleanCode.substring(stringStart, stringEnd))
                pop()
                index = stringEnd
                continue
            }

            // Numbers
            if (char.isDigit()) {
                val numStart = index
                while (index < length && (cleanCode[index].isDigit() || cleanCode[index] == '.')) {
                    index++
                }
                pushStyle(SpanStyle(color = numberColor))
                append(cleanCode.substring(numStart, index))
                pop()
                continue
            }

            // Words/Identifiers matching
            if (char.isLetter() || char == '_') {
                val wordStart = index
                while (index < length && (cleanCode[index].isLetterOrDigit() || cleanCode[index] == '_')) {
                    index++
                }
                val word = cleanCode.substring(wordStart, index)
                
                when {
                    keywords.contains(word) -> {
                        pushStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    }
                    keyTypes.contains(word) -> {
                        pushStyle(SpanStyle(color = typeColor))
                        append(word)
                        pop()
                    }
                    // Simple function call guess: followed immediately by parenthesis
                    index < length && cleanCode[index] == '(' -> {
                        pushStyle(SpanStyle(color = functionColor))
                        append(word)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = normalColor))
                        append(word)
                        pop()
                    }
                }
                continue
            }

            // Default fallback
            pushStyle(SpanStyle(color = normalColor))
            append(char)
            pop()
            index++
        }
    }
}
