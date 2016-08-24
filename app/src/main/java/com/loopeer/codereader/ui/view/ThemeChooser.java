package com.loopeer.codereader.ui.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ThemeChooser {
    public interface OnItemSelectListener {
        void onItemSelect(int id, String tag);
    }
    private HashMap<Integer, String> mViewThemeTags;
    private Context mContext;
    private OnItemSelectListener mOnItemSelectListener;

    public ThemeChooser(Context context, OnItemSelectListener onItemSelectListener) {
        mContext = context;
        mOnItemSelectListener = onItemSelectListener;
        mViewThemeTags = new HashMap<>();
    }

    public void addItem(int id, String tag) {
        mViewThemeTags.put(id, tag);
    }

    public void onItemSelect(View view) {
        view.setSelected(true);
        mOnItemSelectListener.onItemSelect(view.getId(), mViewThemeTags.get(view.getId()));
        for (Integer i : mViewThemeTags.keySet()) {
            if (view.getId() != i) {
                ((Activity) mContext).findViewById(i).setSelected(false);
            }
        }
    }

    public void onItemSelectByTag(String tag) {
        for (Map.Entry<Integer, String> entry : mViewThemeTags.entrySet()) {
            int id = entry.getKey();
            ((Activity) mContext).findViewById(id).setSelected(entry.getValue().equals(tag));
        }
    }

}
