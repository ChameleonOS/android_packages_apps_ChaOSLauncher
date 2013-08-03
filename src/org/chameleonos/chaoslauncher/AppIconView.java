/*
 * Copyright (C) 2013 The ChameleonOS Open Source Project
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

package org.chameleonos.chaoslauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AppIconView extends FrameLayout {
    private BubbleTextView mBubbleTextView;
    private TextView mNotificationCountView;

    public AppIconView(Context context) {
        this(context, null);
    }

    public AppIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBubbleTextView = (BubbleTextView) findViewById(R.id.icon);
        mNotificationCountView = (TextView) findViewById(R.id.notification_count);
    }

    public void setNotificationCount(int count, int id) {
        ItemInfo info = (ItemInfo) getTag();
        Integer idCount = info.mCounts.get(id);
        if (idCount != null) {
            info.mNotificationCount -= idCount;
            info.mCounts.remove(id);
        }

        if (id == -1) {
            info.mCounts.clear();
            info.mNotificationCount = 0;
        }

        if (count > 0) {
            info.mNotificationCount += count;
            info.mCounts.put(id, new Integer(count));
        }

        if (info.mCounts.size() <= 0)
            info.mNotificationCount = 0;

        setNotificationCount(info.mNotificationCount);
    }

    public void setNotificationCount(int count) {
        if (count <= 0) {
            mNotificationCountView.setVisibility(View.GONE);
        } else {
            mNotificationCountView.setText("" + count);
            if (mNotificationCountView.getVisibility() == View.GONE) {
                mNotificationCountView.setVisibility(View.VISIBLE);
                mNotificationCountView.startAnimation(
                        AnimationUtils.loadAnimation(getContext(), R.anim.notification_popup));
            }
        }
    }

    public BubbleTextView getBubbleTextView() {
        return mBubbleTextView;
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        mBubbleTextView.applyFromShortcutInfo(info, iconCache);
        setTag(info);
        setNotificationCount(info.mNotificationCount);
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache, float scale) {
        mBubbleTextView.applyFromShortcutInfo(info, iconCache, scale);
        setTag(info);
        setNotificationCount(info.mNotificationCount);
    }

    public void setTextVisible(boolean visible) {
        mBubbleTextView.setTextVisible(visible);
    }

    public void setStayPressed(boolean stayPressed) {
        mBubbleTextView.setStayPressed(stayPressed);
    }

    int getPressedOrFocusedBackgroundPadding() {
        return mBubbleTextView.getPressedOrFocusedBackgroundPadding();
    }

    Bitmap getPressedOrFocusedBackground() {
        return mBubbleTextView.getPressedOrFocusedBackground();
    }

    void setTextColor(int color) {
        mBubbleTextView.setTextColor(color);
    }

    public void setIconScale(float scale) {
        mBubbleTextView.setIconScale(scale);
    }

    void clearPressedOrFocusedBackground() {
        mBubbleTextView.clearPressedOrFocusedBackground();
    }
}

