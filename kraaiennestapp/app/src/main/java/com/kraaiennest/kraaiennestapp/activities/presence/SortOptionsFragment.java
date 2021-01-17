package com.kraaiennest.kraaiennestapp.activities.presence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import com.kraaiennest.kraaiennestapp.R;
import com.kraaiennest.kraaiennestapp.model.PresenceSortOrder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SortOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortOptionsFragment extends DialogFragment {

    SortDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the SortDialogListener listener; so we can send events to the host
            listener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(requireActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // The 'which' argument contains the index position
        // of the selected item
        builder.setTitle(R.string.sort_field)
                .setItems(R.array.presence_sort_array, (dialog, which) -> listener.onDialogPositiveClick(PresenceSortOrder.values()[which]));
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface SortDialogListener {
        public void onDialogPositiveClick(PresenceSortOrder sortOrder);
    }
}
