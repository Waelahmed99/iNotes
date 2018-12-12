package asana.inotes.fragments;

import android.content.DialogInterface;
import android.content.Intent;
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
import asana.inotes.activities.NoteDetailsActivity;
import asana.inotes.database.NotesDbHelper;

public class FragmentNoteDetails extends Fragment implements IonBackPressInFrag {

    EditText titleView, contentView, labelView;
    String title, content, label;
    long id;
    NotesDbHelper db;
    boolean goBack;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        db = new NotesDbHelper(getContext());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getLong("id", -1);
            title = bundle.getString("title");
            content = bundle.getString("content");
            label = db.getColumnById(id, NotesDbHelper.NotesColumns.COLUMN_LABEL);
        }

        // Find your views here.
        titleView = view.findViewById(R.id.titleView);
        contentView = view.findViewById(R.id.contentView);
        labelView = view.findViewById(R.id.labelView);
        ((NoteDetailsActivity) getActivity()).backPressInFrag = this;
        goBack = true;

        // Put details
        titleView.setText(title);
        contentView.setText(content);
        labelView.setText(label);
    }

    public void updateNote(String newTitle, String newContent, String newLabel) {
        db.deleteItem(id);
        db.insertData(newTitle, newContent, newLabel);
        // Start done animation.
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new FragmentAnimation()).commit();
    }

    @Override
    public void backPressed() {
        String newTitle = titleView.getText().toString();
        String newContent = contentView.getText().toString();
        String newLabel = labelView.getText().toString();

        // No changes happened.
        if (title.equals(newTitle) && content.equals(newContent) && label.equals(newLabel)) {
            ((NoteDetailsActivity) getActivity()).setGoBack(true);
            return;
        }

        // Show dialog at first.
        if (goBack)
            showAlertDialog();
        else
            updateNote(newTitle, newContent, newLabel);
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save changes");
        builder.setMessage("Do you want to save these changes?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Saving confirmed.
                dialog.dismiss();
                goBack = false;
                backPressed();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Saving denied.
                dialog.dismiss();
                ((NoteDetailsActivity) getActivity()).setGoBack(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
