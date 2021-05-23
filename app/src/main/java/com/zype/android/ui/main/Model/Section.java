package com.zype.android.ui.main.Model;

import androidx.fragment.app.Fragment;

/**
 * Created by Evgeny Cherkasov on 04.07.2018
 */
public class Section {
    public int position;
    public String title;

    public Fragment fragment;

    public Section(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }
}
