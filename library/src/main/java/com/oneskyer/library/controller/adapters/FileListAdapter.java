package com.oneskyer.library.controller.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.oneskyer.library.R;
import com.oneskyer.library.controller.NotifyItemChecked;
import com.oneskyer.library.model.DialogConfigs;
import com.oneskyer.library.model.DialogProperties;
import com.oneskyer.library.model.FileListItem;
import com.oneskyer.library.model.MarkedItemList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Adapter Class that extends {@link BaseAdapter} that is
 * used to populate {@link ListView} with file info.
 */
public class FileListAdapter extends BaseAdapter {
    private static final String TAG = FileListAdapter.class.getSimpleName();
    private ArrayList<FileListItem> listItem;
    private Context context;
    private DialogProperties properties;
    private NotifyItemChecked notifyItemChecked;

    public FileListAdapter(ArrayList<FileListItem> listItem, Context context, DialogProperties properties) {
        this.listItem = listItem;
        this.context = context;
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public FileListItem getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_file_list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final FileListItem item = listItem.get(i);
        if (MarkedItemList.hasItem(item.getLocation())) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.marked_item_animation);
            view.setAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.unmarked_item_animation);
            view.setAnimation(animation);
        }
        if (item.isDirectory()) {
            holder.type_icon.setImageResource(R.drawable.ic_type_folder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
            }
            if (properties.selection_type == DialogConfigs.FILE_SELECT) {
                holder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
            }
        } else {
            holder.type_icon.setImageResource(R.drawable.ic_type_file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent, context.getTheme()));
            } else {
                holder.type_icon.setColorFilter(context.getResources().getColor(R.color.colorAccent));
            }
            if (properties.selection_type == DialogConfigs.DIR_SELECT) {
                holder.checkBox.setVisibility(View.INVISIBLE);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
            }
        }
        holder.type_icon.setContentDescription(item.getFilename());
        holder.name.setText(item.getFilename());
        SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stime = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(item.getTime());
        if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
            holder.type.setText(R.string.label_parent_directory);
        } else {
            holder.type.setText(context.getString(R.string.last_edit) + sdate.format(date) + ", " + stime.format(date));
        }
        if (holder.checkBox.getVisibility() == View.VISIBLE) {
            if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
            if (MarkedItemList.hasItem(item.getLocation())) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setMarked(isChecked);
            if (item.isMarked()) {
                if (properties.selection_mode == DialogConfigs.MULTI_MODE) {
                    MarkedItemList.addSelectedItem(item);
                } else {
                    MarkedItemList.addSingleFile(item);
                }
            } else {
                MarkedItemList.removeSelectedItem(item.getLocation());
            }
            notifyItemChecked.notifyCheckBoxIsClicked();
        });
        return view;
    }

    private class ViewHolder {
        ImageView type_icon;
        TextView name, type;
        MaterialCheckBox checkBox;

        ViewHolder(View itemView) {
            name = itemView.findViewById(R.id.file_name);
            type = itemView.findViewById(R.id.file_type);
            type_icon = itemView.findViewById(R.id.image_type);
            checkBox = itemView.findViewById(R.id.file_check);
        }
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }
}
