package asana.inotes.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import asana.inotes.R;
import asana.inotes.activities.AddNoteActivity;
import asana.inotes.database.NotesDbHelper;

public class FragmentAddNote extends Fragment implements IonBackPressInFrag {

    EditText titleView, contentView, labelView;
    boolean goBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Find your views by id here.
        titleView = view.findViewById(R.id.titleView);
        contentView = view.findViewById(R.id.contentView);
        labelView = view.findViewById(R.id.labelView);
        goBack = false;
        ((AddNoteActivity) getActivity()).backPressInFrag = this;

        // ٍٍSave note button click.
        view.findViewById(R.id.saveNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleView.getText().toString().length() != 0 || contentView.getText().toString().length() != 0) {
                    // If there is a title or a content, save the note
                    saveNote();
                } else
                    Toast.makeText(getContext(), "Please enter something", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNote() {
        // Get values to add to database
        String titleText = titleView.getText().toString();
        String contentText = contentView.getText().toString();
        String labelText = labelView.getText().toString();

        // Insert data into database.
        NotesDbHelper db = new NotesDbHelper(getContext());
        db.insertData(titleText, contentText, labelText);

        // Start done animation.
        getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, new FragmentAnimation()).commit();
    }

    @Override
    public void backPressed() {
        // Add notes fragment.
        String titleText = titleView.getText().toString();
        String contentText = contentView.getText().toString();
        // If title or content view are not empty.
        if ((titleText.length() != 0 || contentText.length() != 0) && !goBack) {
            showAlertDialog();
            return;
        }
        goBack = false;
        ((AddNoteActivity) getActivity()).setGoBack(true);
    }

    private void showAlertDialog() {
        // Alert dialog to ensure saving order.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?");
        builder.setMessage("Your note will be deleted");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Saving denied.
                dialog.dismiss();
                goBack = true;
                backPressed();
            }
        });
        builder.setNegativeButton("Save note", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Saving confirmed.
                dialog.dismiss();
                saveNote();
            }
        });
        // Show alert dialog.
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
