package com.tatanstudios.astropollococina.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Cancelar una orden en dos pasos: escribir el motivo (lo ve el cliente) y confirmar.
 * [aviso] es un texto extra opcional (por ejemplo, que la orden ya se está preparando).
 */
@Composable
fun DialogoCancelarOrden(
    idorden: Int,
    onDismiss: () -> Unit,
    onConfirmar: (motivo: String) -> Unit,
    aviso: String? = null,
) {
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var confirmando by remember { mutableStateOf(false) }

    if (confirmando) {
        CustomModal2Botones(
            showDialog = true,
            titulo = "¿Cancelar la orden #$idorden?",
            message = "Motivo: ${motivo.trim()}",
            onDismiss = { confirmando = false },
            onAccept = { onConfirmar(motivo.trim()) },
            txtBtnAceptar = "Sí, cancelar",
            txtBtnCancelar = "No",
            colorAceptar = AppColors.rojoOscuro,
        )
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(22.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Cancelar orden #$idorden", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AppColors.letraPrincipal)
            Text("Escribe el motivo. El cliente lo verá en su app.", style = MaterialTheme.typography.bodyMedium, color = AppColors.grisIcono)
            if (!aviso.isNullOrBlank()) {
                Text(
                    aviso,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.ambar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.ambarFondo, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                )
            }

            OutlinedTextField(
                value = motivo,
                onValueChange = { if (it.length <= 300) { motivo = it; error = false } },
                placeholder = { Text("Motivo de la cancelación") },
                isError = error,
                supportingText = { Text(if (error) "Escribe el motivo de la cancelación" else "${motivo.length}/300") },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.rojo,
                    cursorColor = AppColors.rojo,
                    unfocusedBorderColor = Color(0xFFDADADA)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.grisTexto),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Volver", fontWeight = FontWeight.SemiBold) }
                Button(
                    onClick = { if (motivo.isBlank()) error = true else confirmando = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.rojoOscuro, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Continuar", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
