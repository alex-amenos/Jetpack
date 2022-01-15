package com.alxnophis.jetpack.authentication.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alxnophis.jetpack.authentication.R
import com.alxnophis.jetpack.authentication.ui.contract.AuthenticationMode
import com.alxnophis.jetpack.authentication.ui.contract.PasswordRequirements
import com.alxnophis.jetpack.core.ui.theme.CoreTheme

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun AuthenticationForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    email: String,
    password: String,
    authenticationMode: AuthenticationMode,
    completedPasswordRequirements: List<PasswordRequirements>,
    enableAuthentication: Boolean,
    onEmailChanged: (email: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,
    onAuthenticate: () -> Unit,
    onToggleMode: () -> Unit
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
            authenticationMode = authenticationMode
        )
        Spacer(modifier = Modifier.height(40.dp))
        val passwordFocusRequester = FocusRequester()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment =
                Alignment.CenterHorizontally
            ) {
                EmailInput(
                    modifier = Modifier.fillMaxWidth(),
                    email = email,
                    onEmailChanged = onEmailChanged
                ) {
                    passwordFocusRequester.requestFocus()
                }

                Spacer(modifier = Modifier.height(16.dp))

                PasswordInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(passwordFocusRequester),
                    password = password,
                    onPasswordChanged = onPasswordChanged
                ) {
                    onAuthenticate()
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
                    onAuthenticate = onAuthenticate
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        ToggleAuthenticationMode(
            modifier = Modifier.fillMaxWidth(),
            authenticationMode = authenticationMode,
            toggleAuthentication = { onToggleMode() }
        )
    }
}

@Composable
internal fun AuthenticationTitle(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode
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
        style = MaterialTheme.typography.h6
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier.fillMaxWidth(),
    email: String,
    onEmailChanged: (email: String) -> Unit,
    onNextClicked: () -> Unit
) {
    TextField(
        modifier = modifier,
        value = email,
        singleLine = true,
        onValueChange = { email -> onEmailChanged(email) },
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

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChanged: (email: String) -> Unit,
    onDoneClicked: () -> Unit,
) {
    val isPasswordHidden: MutableState<Boolean> = remember { mutableStateOf(true) }
    TextField(
        modifier = modifier,
        value = password,
        singleLine = true,
        onValueChange = { onPasswordChanged(it) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable(
                    onClickLabel = if (isPasswordHidden.value) {
                        stringResource(R.string.authentication_cd_show_password)
                    } else {
                        stringResource(R.string.authentication_cd_hide_password)
                    }
                ) {
                    isPasswordHidden.value = !isPasswordHidden.value
                },
                imageVector = if (isPasswordHidden.value) {
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
            .padding(6.dp)
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

@Composable
fun AuthenticationButton(
    modifier: Modifier = Modifier,
    authenticationMode: AuthenticationMode,
    enableAuthentication: Boolean,
    onAuthenticate: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = {
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

@Preview(showBackground = true)
@Composable
private fun AuthenticationSighInFormPreview() {
    CoreTheme {
        AuthenticationForm(
            modifier = Modifier.wrapContentSize(),
            authenticationMode = AuthenticationMode.SIGN_IN,
            isLoading = true,
            email = "",
            password = "",
            completedPasswordRequirements = emptyList(),
            enableAuthentication = true,
            onEmailChanged = {},
            onPasswordChanged = {},
            onAuthenticate = {},
            onToggleMode = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthenticationSignUpFormPreview() {
    CoreTheme {
        AuthenticationForm(
            modifier = Modifier.wrapContentSize(),
            authenticationMode = AuthenticationMode.SIGN_UP,
            isLoading = true,
            email = "",
            password = "",
            completedPasswordRequirements = listOf(
                PasswordRequirements.CAPITAL_LETTER,
                PasswordRequirements.EIGHT_CHARACTERS,
                PasswordRequirements.NUMBER,
            ),
            enableAuthentication = true,
            onEmailChanged = {},
            onPasswordChanged = {},
            onAuthenticate = {},
            onToggleMode = {},
        )
    }
}
