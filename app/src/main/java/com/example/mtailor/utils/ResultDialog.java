package com.example.mtailor.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mtailor.R;

public class ResultDialog extends DialogFragment {

    private Activity activity;
    private boolean result;

    public ResultDialog(Activity activity, boolean result){
        this.activity = activity;
        this.result = result;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;
        Button okBtn;
//        different layouts for success or failure
        if (result){
            view = inflater.inflate(R.layout.dialog_success, container, false);
            okBtn = view.findViewById(R.id.success_ok_btn);
        }
        else {
            view = inflater.inflate(R.layout.dialog_failure, container, false);
            okBtn = view.findViewById(R.id.failure_ok_btn);
        }

//        on click for Ok button
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.finish();
            }
        });

        setCancelable(false);
        return view;
    }
}
