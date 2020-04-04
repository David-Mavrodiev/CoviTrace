package com.covitrack.david.covitrack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class Dialog extends AppCompatDialogFragment {
    private DialogListener listener;
    private String content;
    private String action;

    public void setAction(String flag) {
        this.action = flag;
    }

    public void setContent(String text) {
        this.content = text;
    }

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        // Set text content for the dialog
        TextView contentTextView = view.findViewById(R.id.dialogText);
        contentTextView.setText(this.content);

        builder.setView(view)
            .setTitle(getResources().getString(R.string.confirm_dialog_title))
            .setNegativeButton(getString(R.string.confirm_dialog_cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.apply(false, action);
                }
            })
            .setPositiveButton(getString(R.string.confirm_dialog_sure),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.apply(true, action);
                }
            });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString() +
                    " activity should implement DialogListener interface");
        }
    }

    public interface DialogListener {
        void apply(Boolean state, String action);
    }
}
