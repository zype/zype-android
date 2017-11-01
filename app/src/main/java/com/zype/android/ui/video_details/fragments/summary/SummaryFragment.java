package com.zype.android.ui.video_details.fragments.summary;


import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ns.developer.tagview.widget.TagCloudLinkView;
import com.zype.android.R;
import com.zype.android.core.provider.CursorHelper;
import com.zype.android.core.provider.helpers.VideoHelper;
import com.zype.android.ui.base.BaseFragment;
import com.zype.android.webapi.model.video.VideoData;

public class SummaryFragment extends BaseFragment {
    private static final String ARG_VIDEO_ID = "video_id";
    private String videoId;

    public SummaryFragment() {
    }

    public static SummaryFragment newInstance(String videoId) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VIDEO_ID, videoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videoId = getArguments().getString(ARG_VIDEO_ID);
        } else {
            throw new IllegalStateException("VideoId can not be empty");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_summary, container, false);
        Cursor cursor = CursorHelper.getVideoCursor(getActivity().getContentResolver(), videoId);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                VideoData video = VideoHelper.objectFromCursor(cursor);
                ((TextView) view.findViewById(R.id.textVideoTitle)).setText(video.getTitle());
                ((TextView) view.findViewById(R.id.textVideoDescription)).setText(video.getDescription());
//                TagCloudLinkView tagCloudView = (TagCloudLinkView) view.findViewById(R.id.tag_cloud);
               //hide keywords
               /* if (video.getKeywords() != null) {
                    for (int i = 0; i < video.getKeywords().size(); i++) {
                        tagCloudView.add(new Tag(i, video.getKeywords().get(i)));
                    }
                    tagCloudView.drawTags();
                    tagCloudView.setOnTagSelectListener(new TagCloudLinkView.OnTagSelectListener() {
                        @Override
                        public void onTagSelected(Tag tag, int i) {
//                            UiUtils.showWarningSnackbar(view, tag.getText());
                        }
                    });
                }*/
            } else {
                throw new IllegalStateException("DB not contains video with ID=" + videoId);
            }
            cursor.close();
        }
        return view;
    }

    @Override
    protected String getFragmentName() {
        return getString(R.string.fragment_name_summary);
    }
}
