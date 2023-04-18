package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationEvent
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.base.constants.EMPTY
import com.alxnophis.jetpack.core.ui.composable.autofill
import com.alxnophis.jetpack.core.ui.theme.AppTheme
import com.alxnophis.jetpack.core.ui.theme.extraLargePadding
import com.alxnophis.jetpack.core.ui.theme.extraSmallPadding
import com.alxnophis.jetpack.core.ui.theme.largePadding
import com.alxnophis.jetpack.core.ui.theme.mediumPadding

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationForm(
    isLoading: Boolean,
    email: String,
    password: String,
    authenticationMode: AuthenticationMode,
    completedPasswordRequirements: List<PasswordRequirements>,
    enableAuthentication: Boolean,
    modifier: Modifier = Modifier,
    handleEvent: AuthenticationEvent.() -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthenticationTitle(
            authenticationMode = authenticationMode,
            modifier = Modifier.clickable {
                if (authenticationMode == AuthenticationMode.SIGN_IN) {
                    handleEvent.invoke(AuthenticationEvent.AutoCompleteAuthorization)
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        val passwordFocusRequester = FocusRequester()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = extraLargePadding)
        ) {
            Column(
                modifier = Modifier.padding(mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val onEmailChanged: (String) -> Unit = { email ->
                    handleEvent(AuthenticationEvent.EmailChanged(email))
                }
                EmailInput(
                    email = email,
                    onEmailChanged = onEmailChanged,
                    onNextClicked = { passwordFocusRequester.requestFocus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .autofill(
                            autofillTypes = listOf(AutofillType.EmailAddress),
                            onFill = onEmailChanged
                        )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val onPasswordChanged: (String) -> Unit = { password ->
                    handleEvent(AuthenticationEvent.PasswordChanged(password))
                }
                PasswordInput(
                    password = password,
                    onPasswordChanged = onPasswordChanged,
                    onDoneClicked = { handleEvent(AuthenticationEvent.Authenticate) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester)
                        .autofill(
                            autofillTypes = listOf(AutofillType.Password),
                            onFill = onPasswordChanged
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = authenticationMode == AuthenticationMode.SIGN_UP
                ) {
                    PasswordRequirementsView(
                        modifier = Modifier.fillMaxWidth(),
                        satisfiedRequirements = completedPasswordRequirements
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AuthenticationButton(
                    enableAuthentication = enableAuthentication,
                    authenticationMode = authenticationMode,
                    onAuthenticate = { handleEvent(AuthenticationEvent.Authenticate) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ToggleAuthenticationMode(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
            authenticationMode = authenticationMode,
            toggleAuthentication = { handleEvent(AuthenticationEvent.ToggleAuthenticationMode) }
        )
    }
}

@Composable
internal fun AuthenticationTitle(
    authenticationMode: AuthenticationMode,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(
            if (authenticationMode == AuthenticationMode.SIGN_IN) {
                R.string.authentication_label_sign_in_to_account
            } else {
                R.string.authentication_label_sign_up_for_account
            }
        ),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailInput(
    email: String,
    onEmailChanged: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    onNextClicked: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = email,
        singleLine = true,
        onValueChange = { emailChanged -> onEmailChanged(emailChanged) },
        label = {
            Text(
                text = stringResource(R.string.authentication_label_email),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNextClicked() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun PasswordInput(
    password: String,
    onPasswordChanged: (email: String) -> Unit,
    onDoneClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordHidden by remember { mutableStateOf(true) }
    OutlinedTextField(
        modifier = modifier,
        value = password,
        singleLine = true,
        onValueChange = { onPasswordChanged(it) },
        visualTransformation = when {
            isPasswordHidden -> PasswordVisualTransformation()
            else -> VisualTransformation.None
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable(
                    onClickLabel = if (isPasswordHidden) {
                        stringResource(R.string.authentication_cd_show_password)
                    } else {
                        stringResource(R.string.authentication_cd_hide_password)
                    }
                ) {
                    isPasswordHidden = !isPasswordHidden
                },
                imageVector = if (isPasswordHidden) {
                    Icons.Default.Visibility
                } else {
                    Icons.Default.VisibilityOff
                },
                contentDescription = null
            )
        },
        label = {
            Text(
                text = stringResource(id = R.string.authentication_label_password),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClicked()
                keyboardController?.hide()
            }
        )
    )
}

@Composable
fun Requirement(
    message: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(12.dp),
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = tint
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.clearAndSetSemantics { },
            text = message,
            fontSize = 12.sp,
            color = tint
        )
    }
}

@Composable
fun PasswordRequirementsView(
    satisfiedRequirements: List<PasswordRequirements>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        PasswordRequirements.values().forEach { requirement ->
            val satisfied = satisfiedRequirements.contains(requirement)
            val message = stringResource(requirement.label)
            val requirementStatus = if (satisfied) {
                stringResource(R.string.authentication_password_requirement_satisfied, message)
            } else {
                stringResource(R.string.authentication_password_requirement_not_satisfied, message)
            }
            Requirement(
                message = requirementStatus,
                tint = if (satisfied) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                },
                modifier = Modifier
                    .padding(extraSmallPadding)
                    .semantics(mergeDescendants = true) {
                        text = AnnotatedString(requirementStatus)
                    }
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun AuthenticationButton(
    authenticationMode: AuthenticationMode,
    enableAuthentication: Boolean,
    modifier: Modifier = Modifier,
    onAuthenticate: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Button(
        modifier = modifier,
        onClick = {
            keyboardController?.hide()
            onAuthenticate()
        },
        enabled = enableAuthentication
    ) {
        Text(
            text = stringResource(
                if (authenticationMode == AuthenticationMode.SIGN_IN) {
                    R.string.authentication_action_sign_in
                } else {
                    R.string.authentication_action_sign_up
                }
            )
        )
    }
}

@Composable
fun ToggleAuthenticationMode(
    authenticationMode: AuthenticationMode,
    modifier: Modifier = Modifier,
    toggleAuthentication: () -> Unit
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable { toggleAuthentication() }
                .padding(largePadding),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            text = stringResource(
                if (authenticationMode == AuthenticationMode.SIGN_IN) {
                    R.string.authentication_action_need_account
                } else {
                    R.string.authentication_action_already_have_account
                }
            )
        )
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
private fun AuthenticationSighInFormPreview() {
    AppTheme {
        AuthenticationForm(
            modifier = Modifier.wrapContentSize(),
            authenticationMode = AuthenticationMode.SIGN_IN,
            isLoading = true,
            email = EMPTY,
            password = EMPTY,
            completedPasswordRequirements = emptyList(),
            enableAuthentication = true,
            handleEvent = {}
        )
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
private fun AuthenticationSignUpFormPreview() {
    AppTheme {
        AuthenticationForm(
            modifier = Modifier.wrapContentSize(),
            authenticationMode = AuthenticationMode.SIGN_UP,
            isLoading = true,
            email = EMPTY,
            password = EMPTY,
            completedPasswordRequirements = listOf(
                PasswordRequirements.CAPITAL_LETTER,
                PasswordRequirements.EIGHT_CHARACTERS,
                PasswordRequirements.NUMBER
            ),
            enableAuthentication = true,
            handleEvent = {}
        )
    }
}
