/*
 * Copyright (C) 2010 The Android Open Source Project
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

/**
 * An icon on a PagedView, specifically for items in the launcher's paged view (with compound
 * drawables on the top).
 */
public class PagedViewIcon extends FrameLayout {
    /** A simple callback interface to allow a PagedViewIcon to notify when it has been pressed */
    public static interface PressedCallback {
        void iconPressed(PagedViewIcon icon);
    }

    @SuppressWarnings("unused")
    private static final String TAG = "ChaOSLauncher.PagedViewIcon";
    private static final float PRESS_ALPHA = 0.4f;

    private PagedViewIcon.PressedCallback mPressedCallback;
    private boolean mLockDrawableState = false;

    private Bitmap mIcon;

    private BubbleTextView mBubbleTextView;
    private TextView mNotificationCountView;

    public PagedViewIcon(Context context) {
        this(context, null);
    }

    public PagedViewIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewIcon(Context context, AttributeSet attrs, int defStyle) {
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

    public void applyFromApplicationInfo(ApplicationInfo info, boolean scaleUp,
            PagedViewIcon.PressedCallback cb) {
        mIcon = info.iconBitmap;
        mPressedCallback = cb;
        mBubbleTextView.setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(mIcon), null, null);
        mBubbleTextView.setCompoundDrawablePadding(0);
        mBubbleTextView.setText(info.title);
        setTag(info);
        setNotificationCount(info.mNotificationCount);
    }

    public void applyFromApplicationInfo(ApplicationInfo info, float scale, boolean scaleUp,
            PagedViewIcon.PressedCallback cb) {
        mIcon = info.iconBitmap;
        int width = (int)((float)mIcon.getWidth() * scale);
        int height = (int)((float)mIcon.getHeight() * scale);
        FastBitmapDrawable d = new FastBitmapDrawable(Bitmap.createScaledBitmap(mIcon,
                width, height, true));
        mPressedCallback = cb;
        mBubbleTextView.setCompoundDrawablesWithIntrinsicBounds(null, d, null, null);
        mBubbleTextView.setCompoundDrawablePadding(0);
        mBubbleTextView.setText(info.title);
        setTag(info);
        setNotificationCount(info.mNotificationCount);
    }

    public void lockDrawableState() {
        mLockDrawableState = true;
    }

    public void resetDrawableState() {
        mLockDrawableState = false;
        post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();

        // We keep in the pressed state until resetDrawableState() is called to reset the press
        // feedback
        if (isPressed()) {
            setAlpha(PRESS_ALPHA);
            if (mPressedCallback != null) {
                mPressedCallback.iconPressed(this);
            }
        } else if (!mLockDrawableState) {
            setAlpha(1f);
        }
    }
}
