/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.horologist.datalayer.sample.screens.inappprompts

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.horologist.datalayer.sample.R

@Composable
fun InstallAppPromptDemo2Screen(
    modifier: Modifier = Modifier,
    viewModel: InstallAppPromptDemo2ViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state == InstallAppPromptDemo2ScreenState.Idle) {
        SideEffect {
            viewModel.initialize()
        }
    }

    val context = LocalContext.current

    InstallAppPromptDemo2Screen(
        state = state,
        onRunDemoClick = viewModel::onRunDemoClick,
        getInstallPromptIntent = {
            viewModel.phoneUiDataLayerHelper.getInstallPromptIntent(
                context = context,
                appPackageName = context.packageName,
                image = R.drawable.watch_app_screenshot,
                topMessage = context.getString(R.string.install_app_prompt_demo2_prompt_top_message),
                bottomMessage = context.getString(R.string.install_app_prompt_demo2_prompt_bottom_message),
            )
        },
        onInstallPromptLaunched = viewModel::onInstallPromptLaunched,
        onInstallPromptInstallClick = viewModel::onInstallPromptInstallClick,
        onInstallPromptCancel = viewModel::onInstallPromptCancel,
        modifier = modifier,
    )
}

@Composable
fun InstallAppPromptDemo2Screen(
    state: InstallAppPromptDemo2ScreenState,
    onRunDemoClick: () -> Unit,
    getInstallPromptIntent: () -> Intent,
    onInstallPromptLaunched: () -> Unit,
    onInstallPromptInstallClick: () -> Unit,
    onInstallPromptCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            onInstallPromptInstallClick()
        } else {
            onInstallPromptCancel()
        }
    }

    Column(
        modifier = modifier.padding(all = 10.dp),
    ) {
        Text(text = stringResource(id = R.string.install_app_prompt_api_call_demo2_message))

        Button(
            onClick = onRunDemoClick,
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally),
            enabled = state != InstallAppPromptDemo2ScreenState.ApiNotAvailable,
        ) {
            Text(text = stringResource(id = R.string.install_app_prompt_run_demo2_button_label))
        }

        when (state) {
            InstallAppPromptDemo2ScreenState.Idle,
            InstallAppPromptDemo2ScreenState.Loaded,
            -> {
                /* do nothing */
            }

            InstallAppPromptDemo2ScreenState.Loading -> {
                CircularProgressIndicator()
            }

            is InstallAppPromptDemo2ScreenState.WatchFound -> {
                SideEffect { launcher.launch(getInstallPromptIntent()) }

                onInstallPromptLaunched()
            }

            InstallAppPromptDemo2ScreenState.WatchNotFound -> {
                Text(
                    stringResource(
                        id = R.string.install_app_prompt_demo2_result_label,
                        stringResource(id = R.string.install_app_prompt_demo2_no_watches_found_label),
                    ),
                )
            }

            InstallAppPromptDemo2ScreenState.InstallPromptInstallClicked -> {
                Text(
                    stringResource(
                        id = R.string.install_app_prompt_demo2_result_label,
                        stringResource(id = R.string.install_app_prompt_demo2_prompt_install_result_label),
                    ),
                )
            }

            InstallAppPromptDemo2ScreenState.InstallPromptInstallCancelled -> {
                Text(
                    stringResource(
                        id = R.string.install_app_prompt_demo2_result_label,
                        stringResource(id = R.string.install_app_prompt_demo2_prompt_cancel_result_label),
                    ),
                )
            }

            InstallAppPromptDemo2ScreenState.ApiNotAvailable -> {
                Text(stringResource(id = R.string.wearable_message_api_unavailable))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InstallAppPromptDemo2ScreenPreview() {
    InstallAppPromptDemo2Screen(
        state = InstallAppPromptDemo2ScreenState.Idle,
        onRunDemoClick = { },
        getInstallPromptIntent = { Intent() },
        onInstallPromptLaunched = { },
        onInstallPromptInstallClick = { },
        onInstallPromptCancel = { },
    )
}
