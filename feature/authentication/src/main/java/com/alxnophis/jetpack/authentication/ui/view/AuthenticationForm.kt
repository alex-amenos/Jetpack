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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.alxnophis.jetpack.core.ui.theme.mediumPadding

@ExperimentalComposeUiApi
@Composable
internal fun AuthenticationForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    email: String,
    password: String,
    authenticationMode: AuthenticationMode,
    completedPasswordRequirements: List<PasswordRequirements>,
    enableAuthentication: Boolean,
    handleEvent: AuthenticationEvent.() -> Unit,
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
                color = MaterialTheme.colors.secondary,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        AuthenticationTitle(
            authenticationMode = authenticationMode,
            handleEvent = handleEvent
        )

        Spacer(modifier = Modifier.height(40.dp))

        val passwordFocusRequester = FocusRequester()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = extraLargePadding),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(mediumPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmailInput(
                    modifier = Modifier.fillMaxWidth(),
                    email = email,
                    onEmailChanged = { email -> handleEvent(AuthenticationEvent.EmailChanged(email)) }
                ) {
                    passwordFocusRequester.requestFocus()
                }

                Spacer(modifier = Modifier.height(16.dp))

                PasswordInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    password = password,
                    onPasswordChanged = { password -> handleEvent(AuthenticationEvent.PasswordChanged(password)) }
                ) {
                    handleEvent(AuthenticationEvent.Authenticate)
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = authenticationMode == AuthenticationMode.SIGN_UP
                ) {
                    PasswordRequirementsView(
                        modifier = Modifier.fillMaxWidth(),
                        satisfiedRequirements = completedPasswordRequirements,
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
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    handleEvent: AuthenticationEvent.() -> Unit,
) {
    Text(
        modifier = modifier.clickable {
            if (authenticationMode == AuthenticationMode.SIGN_IN) {
                handleEvent.invoke(AuthenticationEvent.AutoCompleteAuthorization)
            }
        },
        text = stringResource(
            if (authenticationMode == AuthenticationMode.SIGN_IN) {
                R.string.authentication_label_sign_in_to_account
            } else {
                R.string.authentication_label_sign_up_for_account
            }
        ),
        style = MaterialTheme.typography.h6
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChanged: (email: String) -> Unit,
    onNextClicked: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .autofill(
                autofillTypes = listOf(AutofillType.EmailAddress),
                onFill = { onEmailChanged(it) },
            ),
        value = email,
        singleLine = true,
        onValueChange = { emailChanged -> onEmailChanged(emailChanged) },
        label = {
            Text(
                text = stringResource(R.string.authentication_label_email),
                style = MaterialTheme.typography.body1,
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

@ExperimentalComposeUiApi
@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChanged: (email: String) -> Unit,
    onDoneClicked: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isPasswordHidden by remember { mutableStateOf(true) }
    OutlinedTextField(
        modifier = modifier
            .autofill(
                autofillTypes = listOf(AutofillType.Password),
                onFill = { onPasswordChanged(it) },
            ),
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
                style = MaterialTheme.typography.body1
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
    modifier: Modifier = Modifier,
    message: String,
    satisfied: Boolean
) {
    val tint = if (satisfied) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = 0.4f)
    }
    val requirementStatus = if (satisfied) {
        stringResource(R.string.authentication_password_requirement_satisfied, message)
    } else {
        stringResource(R.string.authentication_password_requirement_not_satisfied, message)
    }
    Row(
        modifier = modifier
            .padding(extraSmallPadding)
            .semantics(mergeDescendants = true) {
                text = AnnotatedString(requirementStatus)
            },
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
    modifier: Modifier = Modifier,
    satisfiedRequirements: List<PasswordRequirements>
) {
    Column(
        modifier = modifier
    ) {
        PasswordRequirements.values().forEach { requirement ->
            Requirement(
                message = stringResource(requirement.label),
                satisfied = satisfiedRequirements.contains(requirement)
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun AuthenticationButton(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    enableAuthentication: Boolean,
    onAuthenticate: () -> Unit,
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
                if (authenticationMode ==
                    AuthenticationMode.SIGN_IN
                ) {
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
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    toggleAuthentication: () -> Unit
) {
    Surface(
        modifier = modifier,
        elevation = 8.dp
    ) {
        TextButton(
            modifier = Modifier.background(MaterialTheme.colors.surface),
            onClick = {
                toggleAuthentication()
            }
        ) {
            Text(
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface,
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
            handleEvent = {},
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
                PasswordRequirements.NUMBER,
            ),
            enableAuthentication = true,
            handleEvent = {},
        )
    }
}
