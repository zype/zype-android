package com.zype.android.ui.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.zype.android.R;

/**
 * Created by Evgeny Cherkasov on 22.03.2018.
 */

public class PlayerControlView extends MediaController {

    private ImageButton buttonCC;

    private Context context;
    private IClosedCaptionsListener listenerCC = null;

    public interface IClosedCaptionsListener {
        void onClickClosedCaptions();
    }

    public PlayerControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PlayerControlView(Context context, boolean useFastForward) {
        super(context, useFastForward);
        this.context = context;
    }

    public PlayerControlView(Context context) {
        super(context, true);
        this.context = context;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;

        View viewCC = makeClosedCaptionsView();
        ((ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(0)).addView(viewCC, frameParams);
    }

    private View makeClosedCaptionsView() {
        buttonCC = new ImageButton(context);
        buttonCC.setImageResource(R.drawable.ic_closed_caption_white_24dp);
        buttonCC.setBackground(null);
        buttonCC.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listenerCC != null) {
                    listenerCC.onClickClosedCaptions();
                }
            }
        });
        return buttonCC;
    }

    public void setClosedCaptionsListener(IClosedCaptionsListener listener) {
        this.listenerCC = listener;
    }

    public void showCC() {
        buttonCC.setVisibility(VISIBLE);
    }

    public void hideCC() {
        buttonCC.setVisibility(GONE);
    }
}

