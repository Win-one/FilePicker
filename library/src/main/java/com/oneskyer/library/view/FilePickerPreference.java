package com.oneskyer.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

import com.oneskyer.library.R;
import com.oneskyer.library.controller.DialogSelectionListener;
import com.oneskyer.library.model.DialogConfigs;
import com.oneskyer.library.model.DialogProperties;

import java.io.File;


public class FilePickerPreference extends Preference implements
        DialogSelectionListener,
        Preference.OnPreferenceClickListener {
    private FilePickerDialog mDialog;
    private DialogProperties properties;
    private String titleText = null;

    public FilePickerPreference(Context context) {
        super(context);
        properties = new DialogProperties();
        setOnPreferenceClickListener(this);
    }

    public FilePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        properties = new DialogProperties();
        initProperties(attrs);
        setOnPreferenceClickListener(this);
    }

    public FilePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        properties = new DialogProperties();
        initProperties(attrs);
        setOnPreferenceClickListener(this);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return super.onGetDefaultValue(a, index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (mDialog == null || !mDialog.isShowing()) {
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.dialogBundle = mDialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        showDialog(myState.dialogBundle);
    }

    private void showDialog(Bundle state) {
        mDialog = new FilePickerDialog(getContext());
        setProperties(properties);
        mDialog.setDialogSelectionListener(this);
        if (state != null) {
            mDialog.onRestoreInstanceState(state);
        }
        mDialog.setTitle(titleText);
        mDialog.show();
    }

    @Override
    public void onSelectedFilePaths(String[] files) {
        StringBuilder buff = new StringBuilder();
        for (String path : files) {
            buff.append(path).append(":");
        }
        String dFiles = buff.toString();
        if (isPersistent()) {
            persistString(dFiles);
        }
        try {
            getOnPreferenceChangeListener().onPreferenceChange(this, dFiles);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        showDialog(null);
        return false;
    }

    public void setProperties(DialogProperties properties) {
        mDialog.setProperties(properties);
    }

    private static class SavedState extends BaseSavedState {
        Bundle dialogBundle;

        public SavedState(Parcel source) {
            super(source);
            dialogBundle = source.readBundle(getClass().getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    private void initProperties(AttributeSet attrs) {
        TypedArray tarr = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.FilePickerPreference, 0, 0);
        final int N = tarr.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = tarr.getIndex(i);
            if (attr == R.styleable.FilePickerPreference_selection_mode) {
                properties.selection_mode = tarr.getInteger(R.styleable.FilePickerPreference_selection_mode, DialogConfigs.SINGLE_MODE);
            } else if (attr == R.styleable.FilePickerPreference_selection_type) {
                properties.selection_type = tarr.getInteger(R.styleable.FilePickerPreference_selection_type, DialogConfigs.FILE_SELECT);
            } else if (attr == R.styleable.FilePickerPreference_root_dir) {
                String root_dir = tarr.getString(R.styleable.FilePickerPreference_root_dir);
                if (root_dir != null && !root_dir.equals("")) {
                    properties.root = new File(root_dir);
                }
            } else if (attr == R.styleable.FilePickerPreference_error_dir) {
                String error_dir = tarr.getString(R.styleable.FilePickerPreference_error_dir);
                if (error_dir != null && !error_dir.equals("")) {
                    properties.error_dir = new File(error_dir);
                }
            } else if (attr == R.styleable.FilePickerPreference_offset_dir) {
                String offset_dir = tarr.getString(R.styleable.FilePickerPreference_offset_dir);
                if (offset_dir != null && !offset_dir.equals("")) {
                    properties.offset = new File(offset_dir);
                }
            } else if (attr == R.styleable.FilePickerPreference_extensions) {
                String extensions = tarr.getString(R.styleable.FilePickerPreference_extensions);
                if (extensions != null && !extensions.equals("")) {
                    properties.extensions = extensions.split(":");
                }
            } else if (attr == R.styleable.FilePickerPreference_title_text) {
                titleText = tarr.getString(R.styleable.FilePickerPreference_title_text);
            }
        }
        tarr.recycle();
    }
}
