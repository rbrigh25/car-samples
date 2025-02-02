/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.car.app.sample.places.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.Session;
import androidx.car.app.SessionInfo;
import androidx.car.app.validation.HostValidator;
import androidx.media.MediaBrowserServiceCompat;

/**
 * Entry point for the app.
 *
 * <p>{@link CarAppService} is the main interface between the app and the car host. For more
 * details, see the <a href="https://developer.android.com/training/cars/navigation">Android for
 * Cars Library developer guide</a>.
 */
public class PlacesCarAppService extends CarAppService {
    @Override
    @NonNull
    public Session onCreateSession(@NonNull SessionInfo sessionInfo) {
        return new Session() {
            @Override
            @NonNull
            public Screen onCreateScreen(@NonNull Intent intent) {
                if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    return PlaceCategoryListScreen.create(getCarContext());
                }

                // If we do not have the location permission, show the request permission screen.
                ScreenManager screenManager = getCarContext().getCarService(ScreenManager.class);
                screenManager
                        .push(PlaceCategoryListScreen.create(getCarContext()));
                return new RequestPermissionScreen(getCarContext(), () -> screenManager.pop());
            }
        };
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        } else {
            return new HostValidator.Builder(getApplicationContext())
                    .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                    .build();
        }
    }
}
