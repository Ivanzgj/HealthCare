package com.ivan.healthcare.healthcare_android.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import com.ivan.healthcare.healthcare_android.R;

/**
 * 自定义对话框构造器
 * Created by Ivan on 16/4/10.
 */
public class DialogBuilder {

    private int titleRes = -1;
    private String title;
    private int contentRes = -1;
    private String content;
    private int positiveRes = -1;
    private String positive;
    private int negativeRes = -1;
    private String negative;
    private DialogInterface.OnClickListener onPositiveClickListener;
    private DialogInterface.OnClickListener onNegaitiveClickListener;
    private View customView;
    private boolean cancellable = true;
    private AlertDialog.Builder dialog;
    private Context context;

    public DialogBuilder(Context context) {
        this.context = context;
    }

    public DialogBuilder create() {
        dialog = new AlertDialog.Builder(context);
        return this;
    }

    public DialogBuilder setTitle(int titleRes) {
        this.titleRes = titleRes;
        return this;
    }

    public DialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogBuilder setContent(int contentRes) {
        this.contentRes = contentRes;
        return this;
    }

    public DialogBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public DialogBuilder setPositive(String positive) {
        this.positive = positive;
        return this;
    }

    public DialogBuilder setPositive(int positiveRes) {
        this.positiveRes = positiveRes;
        return this;
    }

    public DialogBuilder setNegative(String negative) {
        this.negative = negative;
        return this;
    }

    public DialogBuilder setNegative(int negativeRes) {
        this.negativeRes = negativeRes;
        return this;
    }

    public DialogBuilder setOnPositiveClickListener(DialogInterface.OnClickListener onPositiveClickListener) {
        this.onPositiveClickListener = onPositiveClickListener;
        return this;
    }

    public DialogBuilder setOnNegaitiveClickListener(DialogInterface.OnClickListener onNegaitiveClickListener) {
        this.onNegaitiveClickListener = onNegaitiveClickListener;
        return this;
    }

    public DialogBuilder setCustomView(View customView) {
        this.customView = customView;
        return this;
    }

    public DialogBuilder setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
        return this;
    }

    public Dialog show() {
        dialog.setCancelable(cancellable);

        if (titleRes != -1) {
            title = context.getResources().getString(titleRes);
        }
        if (title != null) {
            dialog.setTitle(title);
        }
        if (contentRes != -1) {
            content = context.getResources().getString(contentRes);
        }

        if (customView == null) {
            if (content != null) {
                dialog.setMessage(content);
            }
        } else {
            dialog.setView(customView);
        }

        if (positiveRes != -1) {
            positive = context.getResources().getString(positiveRes);
        }
        if (positive != null) {
            dialog.setPositiveButton(positive, onPositiveClickListener);
        }
        if (negativeRes != -1) {
            negative = context.getResources().getString(negativeRes);
        }
        if (negative != null) {
            dialog.setNegativeButton(negative, onNegaitiveClickListener);
        }

        Dialog d = dialog.create();
        d.show();
        Compat.fixDialogStyle(d);
        return d;
    }
}
