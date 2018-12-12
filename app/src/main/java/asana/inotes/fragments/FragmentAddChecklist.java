package asana.inotes.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import asana.inotes.R;
import asana.inotes.activities.AddChecklistActivity;
import asana.inotes.database.ChecklistsDbHelper;
import asana.inotes.model.ListsModel;

public class FragmentAddChecklist extends Fragment implements IonBackPressInFrag {

    EditText titleView, itemView;
    List<EditText> editTextList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_checklist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Find your views by id here.
        titleView = view.findViewById(R.id.checklistTitle);
        itemView = view.findViewById(R.id.checklistItem);
        editTextList = new ArrayList<>();
        editTextList.add(itemView);
        ((AddChecklistActivity) getActivity()).backPressInFrag = this;

        // Add another item.
        view.findViewById(R.id.addAnotherItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLayoutUI();
            }
        });

        // Save data.
        view.findViewById(R.id.saveChecklist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChecklist();
            }
        });

    }

    private void updateLayoutUI() {
        LinearLayout mLayout = getView().findViewById(R.id.checkboxLayout);
        LinearLayout innerLayout = new LinearLayout(getContext());
        CheckBox checkBox = new CheckBox(getContext());
        EditText editText = new EditText(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // EditText properties.
        editText.setLayoutParams(params);
        editText.setHint("Add Item");
        editText.setGravity(Gravity.TOP);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        editText.setTextSize(17);
        editText.setMaxLines(2);
        editText.setPadding(itemView.getPaddingLeft(), 5, 5, 5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        }
        editText.setId(editTextList.size());
        editText.setTextColor(Color.parseColor("#191919"));

        // Checkbox properties.

        checkBox.setLayoutParams(params);
        checkBox.setClickable(false);

        // LinearLayout properties.
        innerLayout.setLayoutParams(params);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.addView(checkBox);
        innerLayout.addView(editText);

        // Finally add our view to main view.
        mLayout.addView(innerLayout);
        editTextList.add(editText);

        // Request keyboard focus "Soft keyboard".
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void saveChecklist() {
        List<ListsModel> itemsList = new ArrayList<>();
        String title = titleView.getText().toString();

        // Must enter title and content inside list.
        if ((editTextList.size() == 1 && itemView.getText().toString().equals("")) || title.equals("")) {
            Toast.makeText(getContext(), "Please enter something, title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Getting inserted data into ArrayList.
        for (int i = 0; i < editTextList.size(); i++) {
            if (!editTextList.get(i).getText().toString().equals("")) {
                String content = editTextList.get(i).getText().toString();
                itemsList.add(new ListsModel(content, "false"));
            }
        }

        // If nothing is inserted.
        if (itemsList.size() == 0)
            return;

        // Insert data into database.
        new ChecklistsDbHelper(getContext()).insertData(title, itemsList);

        // Start done animation
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new FragmentAnimation()).commit();
    }


    @Override
    public void backPressed() {
        String title = titleView.getText().toString();
        String contentText = itemView.getText().toString();

        if ((contentText.equals("")) || title.equals("")) {
            ((AddChecklistActivity) getActivity()).setGoBack(true);
            return;
        }

        showAlertDialog();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Are you sure?");
        builder.setMessage("Your checklist will be deleted");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save denied.
                dialog.dismiss();
                ((AddChecklistActivity) getActivity()).setGoBack(true);
            }
        });
        builder.setNegativeButton("Save checklist", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save confirmed.
                dialog.dismiss();
                saveChecklist();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
