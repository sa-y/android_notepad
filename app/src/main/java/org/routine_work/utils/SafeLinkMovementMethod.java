package org.routine_work.utils;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * A safe version of LinkMovementMethod that catches IndexOutOfBoundsException.
 * This works around a framework bug where getOffsetForPosition returns -1,
 * which is then passed to Selection.setSelection, causing a crash.
 */
public class SafeLinkMovementMethod extends LinkMovementMethod {
    private static SafeLinkMovementMethod sInstance;

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new SafeLinkMovementMethod();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        try {
            return super.onTouchEvent(widget, buffer, event);
        } catch (IndexOutOfBoundsException e) {
            // Log the exception and consume the event to prevent crash
            Log.e("SafeLink", "Caught IndexOutOfBoundsException in LinkMovementMethod", e);
            return true;
        }
    }
}
