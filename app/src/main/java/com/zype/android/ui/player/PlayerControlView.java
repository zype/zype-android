package com.zype.android.ui.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;

import com.zype.android.R;
import com.zype.android.ZypeConfiguration;
import com.zype.android.core.settings.SettingsProvider;

/**
 * Created by Evgeny Cherkasov on 22.03.2018.
 */

public class PlayerControlView extends MediaController {

    private ImageButton buttonCC;

    private Context context;
    private IPlayerControlListener listenerPlayerControl = null;

    public interface IPlayerControlListener {
        void onNext();
        void onPrevious();
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

        ViewGroup rootView = ((ViewGroup) ((ViewGroup) getChildAt(0)).getChildAt(0));
        if (ZypeConfiguration.autoplayEnabled(view.getContext())
                && SettingsProvider.getInstance().getBoolean(SettingsProvider.AUTOPLAY)) {
            View viewNext = makeNextView();
            rootView.addView(viewNext, frameParams);
            View viewPrevious = makePreviousView();
            rootView.addView(viewPrevious, 0, frameParams);
        }
        View viewCC = makeClosedCaptionsView();
        rootView.addView(viewCC, frameParams);
    }

    private View makeClosedCaptionsView() {
        buttonCC = new ImageButton(context);
        buttonCC.setImageResource(R.drawable.ic_closed_caption_white_24dp);
        buttonCC.setBackground(null);
        buttonCC.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listenerPlayerControl != null) {
                    listenerPlayerControl.onClickClosedCaptions();
                }
            }
        });
        return buttonCC;
    }

    private View makeNextView() {
        ImageButton buttonNext = new ImageButton(context);
        buttonNext.setImageResource(R.drawable.baseline_skip_next_white_24);
        buttonNext.setBackground(null);
        buttonNext.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listenerPlayerControl != null) {
                    listenerPlayerControl.onNext();
                }
            }
        });
        return buttonNext;
    }

    private View makePreviousView() {
        ImageButton buttonPrevious = new ImageButton(context);
        buttonPrevious.setImageResource(R.drawable.baseline_skip_previous_white_24);
        buttonPrevious.setBackground(null);
        buttonPrevious.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (listenerPlayerControl != null) {
                    listenerPlayerControl.onPrevious();
                }
            }
        });
        return buttonPrevious;
    }

    public void setPlayerControlListener(IPlayerControlListener listener) {
        this.listenerPlayerControl = listener;
    }

    public void showCC() {
        buttonCC.setVisibility(VISIBLE);
    }

    public void hideCC() {
        buttonCC.setVisibility(GONE);
    }
}

