package com.example.gamescore.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gamescore.R;

public class MiDialogNoReview extends DialogFragment {

    public interface MiDialogNoReviewListener {
        void onCreateReview();

        void onCancelReview();
    }

    private MiDialogNoReviewListener miListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(getString(R.string.dialog_no_review_title))
                .setMessage(getString(R.string.dialog_no_review_message))
                .setPositiveButton(android.R.string.ok, (dialog, id) -> miListener.onCreateReview())
                .setNegativeButton(android.R.string.cancel, (dialog, id) -> miListener.onCancelReview());
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            miListener = (MiDialogNoReviewListener) requireActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException(requireActivity() + " falta implementar listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        miListener = null;
    }
}
