

package com.mail163.email.activity;

import com.mail163.email.Logs;
import com.mail163.email.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * This custom View is the list item for the MessageList activity, and serves two purposes:
 * 1.  It's a container to store message metadata (e.g. the ids of the message, mailbox, & account)
 * 2.  It handles internal clicks such as the checkbox or the favorite star
 */
public class MessageListItem extends RelativeLayout {

    public long mMessageId;
    public long mMailboxId;
    public long mAccountId;
    public boolean mRead;
    public boolean mFavorite;
    public boolean mSelected;
    public boolean mPriority;
    
    private boolean mAllowBatch;
    private MessageList.MessageListAdapter mAdapter;

    private boolean mDownEvent;
    private boolean mCachedViewPositions;
    private int mCheckRight;
    private int mStarLeft;
    private int mMailStateLeft;
    // Padding to increase clickable areas on left & right of each list item
    private final static float CHECKMARK_PAD = 10.0F;
    private final static float STAR_PAD = 10.0F;

    public MessageListItem(Context context) {
        super(context);
    }

    public MessageListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Called by the adapter at bindView() time
     * 
     * @param adapter the adapter that creates this view
     * @param allowBatch true if multi-select is enabled for this list
     */
    public void bindViewInit(MessageList.MessageListAdapter adapter, boolean allowBatch) {
        mAdapter = adapter;
        mAllowBatch = allowBatch;
        mCachedViewPositions = false;
    }

    /**
     * Overriding this method allows us to "catch" clicks in the checkbox or star
     * and process them accordingly.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        int touchX = (int) event.getX();

        if (!mCachedViewPositions) {
            float paddingScale = getContext().getResources().getDisplayMetrics().density;
            int checkPadding = (int) ((CHECKMARK_PAD * paddingScale) + 0.5);
            int starPadding = (int) ((STAR_PAD * paddingScale) + 0.5);
            mCheckRight = findViewById(R.id.favorite).getRight() + checkPadding;
            mStarLeft = findViewById(R.id.selected).getLeft() - starPadding;
            mMailStateLeft = findViewById(R.id.mail_state).getRight();
            mCachedViewPositions = true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                
                mDownEvent = true;
                if ((mAllowBatch && touchX < mCheckRight) || touchX > mStarLeft) {
                    handled = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mDownEvent = false;
                break;

            case MotionEvent.ACTION_UP:
                if (mDownEvent) {
                    if (mAllowBatch && touchX < mCheckRight && touchX > mMailStateLeft) {
                    	 mFavorite = !mFavorite;
                         mAdapter.updateFavorite(this, mFavorite);
                         handled = true;
                    } else if (touchX > mStarLeft) {
                    	 mSelected = !mSelected;
                         mAdapter.updateSelected(this, mSelected);
                         handled = true;
                       
                    }
                }
                break;
        }
        if (handled) {
            postInvalidate();
        } else {
            handled = super.onTouchEvent(event);
        }

        return handled;
    }
}
