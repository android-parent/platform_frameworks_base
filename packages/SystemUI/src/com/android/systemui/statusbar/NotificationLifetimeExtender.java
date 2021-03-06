/*
 * Copyright (C) 2019 The Android Open Source Project
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

package com.android.systemui.statusbar;

import android.annotation.NonNull;

import com.android.systemui.statusbar.NotificationData;

/**
 * Interface for anything that may need to keep notifications managed even after
 * {@link NotificationListener} removes it.  The lifetime extender is in charge of performing the
 * callback when the notification is then safe to remove.
 */
public interface NotificationLifetimeExtender {

    /**
     * Set the handler to callback to when the notification is safe to remove.
     *
     * @param callback the handler to callback
     */
    void setCallback(@NonNull NotificationSafeToRemoveCallback callback);

    /**
     * Determines whether or not the extender needs the notification kept after removal.
     *
     * @param entry the entry containing the notification to check
     * @return true if the notification lifetime should be extended
     */
    boolean shouldExtendLifetime(@NonNull NotificationData.Entry entry);

    /**
     * It's possible that a notification was canceled before it ever became visible. This callback
     * gives lifetime extenders a chance to make sure it shows up. For example if a foreground
     * service is canceled too quickly but we still want to make sure a FGS notification shows.
     * @param pendingEntry the canceled (but pending) entry
     * @return true if the notification lifetime should be extended
     */
    default boolean shouldExtendLifetimeForPendingNotification(
            @NonNull NotificationData.Entry pendingEntry) {
        return false;
    }

    /**
     * Sets whether or not the lifetime should be managed by the extender.  In practice, if
     * shouldManage is true, this is where the extender starts managing the entry internally and is
     * now responsible for calling {@link NotificationSafeToRemoveCallback#onSafeToRemove(String)}
     * when the entry is safe to remove.  If shouldManage is false, the extender no longer needs to
     * worry about it (either because we will be removing it anyway or the entry is no longer
     * removed due to an update).
     *
     * @param entry the entry that needs an extended lifetime
     * @param shouldManage true if the extender should manage the entry now, false otherwise
     */
    void setShouldManageLifetime(@NonNull NotificationData.Entry entry, boolean shouldManage);

    /**
     * The callback for when the notification is now safe to remove (i.e. its lifetime has ended).
     */
    interface NotificationSafeToRemoveCallback {
        /**
         * Called when the lifetime extender determines it's safe to remove.
         *
         * @param key key of the entry that is now safe to remove
         */
        void onSafeToRemove(String key);
    }
}
