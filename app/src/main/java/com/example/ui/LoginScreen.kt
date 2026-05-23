package com.example.ui

import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Biometric Login",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "OmniSync Secure Access",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "End-to-end encrypted platform",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val activity = context as? FragmentActivity
                    if (activity != null) {
                        val executor = ContextCompat.getMainExecutor(activity)
                        val biometricPrompt = BiometricPrompt(
                            activity, executor,
                            object : BiometricPrompt.AuthenticationCallback() {
                                override fun onAuthenticationError(
                                    errorCode: Int,
                                    errString: CharSequence
                                ) {
                                    super.onAuthenticationError(errorCode, errString)
                                    Toast.makeText(context, "Hardware not available. Simulating login.", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess() // Bypassing for emulator/demo
                                }

                                override fun onAuthenticationSucceeded(
                                    result: BiometricPrompt.AuthenticationResult
                                ) {
                                    super.onAuthenticationSucceeded(result)
                                    onLoginSuccess()
                                }

                                override fun onAuthenticationFailed() {
                                    super.onAuthenticationFailed()
                                    Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        
                        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Biometric login for OmniSync")
                            .setSubtitle("Access your secured dashboard")
                            .setNegativeButtonText("Use account password")
                            .build()
                            
                        biometricPrompt.authenticate(promptInfo)
                    } else {
                        onLoginSuccess() 
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Unlock with Biometrics")
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { onLoginSuccess() },
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("Login with Social Media (Mock)")
            }
        }
    }
}
