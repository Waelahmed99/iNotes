package asana.inotes.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import asana.inotes.R;
import asana.inotes.activities.ChecklistDetailsActivity;
import asana.inotes.database.ChecklistsDbHelper;
import asana.inotes.model.ListsModel;

public class FragmentChecklistDetails extends Fragment implements IonBackPressInFrag {

    EditText titleView;
    ChecklistsDbHelper db;
    List<EditText> editTextList;
    List<CheckBox> checkBoxList;
    List<String> stringList;
    List<Boolean> booleanList;
    String title;
    long id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checklist_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getLong("id", -1);
            title = bundle.getString("title");
        }

        // Find your views by id here.
        titleView = view.findViewById(R.id.checklistTitle);
        db = new ChecklistsDbHelper(getContext());
        editTextList = new ArrayList<>();
        checkBoxList = new ArrayList<>();
        stringList = new ArrayList<>();
        booleanList = new ArrayList<>();
        ((ChecklistDetailsActivity) getActivity()).backPressInFrag = this;
        loadList();

        // Put details.
        titleView.setText(title);

        view.findViewById(R.id.addAnotherItem).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLayoutUI();
            }
        });
    }

    private void loadList() {
        List<ListsModel> listsModels = db.findListByID(id);

        for (int i = 0; i < listsModels.size(); i++) {
            ListsModel model = listsModels.get(i);
            LinearLayout mLayout = getActivity().findViewById(R.id.checkboxLayout);
            LinearLayout innerLayout = new LinearLayout(getContext());
            CheckBox checkBox = new CheckBox(getContext());
            final EditText editText = new EditText(getContext());

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
            editText.setPadding(5, 5, 5, 5);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                editText.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
            }
            editText.setText(model.getContent());

            // CheckBox box color.
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked} , // checked
                    },
                    new int[]{
                            Color.parseColor("#808080"),
                            Color.parseColor("#03A9F4"),
                    }
            );
            CompoundButtonCompat.setButtonTintList(checkBox,colorStateList);

            // Checkbox details
            checkBox.setChecked(Boolean.parseBoolean(model.isChecked()));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        editText.setPaintFlags( editText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                }
            });

            // Add line through text if checked.
            if (checkBox.isChecked()) {
                editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                editText.setPaintFlags(editText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }

            // LinearLayout properties.
            innerLayout.setLayoutParams(params);
            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
            innerLayout.addView(checkBox);
            innerLayout.addView(editText);

            // Finally add our view to main view.
            mLayout.addView(innerLayout);

            editTextList.add(editText);
            checkBoxList.add(checkBox);
            stringList.add(model.getContent());
            booleanList.add(Boolean.parseBoolean(model.isChecked()));
        }
    }

    private void updateLayoutUI() {
        LinearLayout mLayout = getView().findViewById(R.id.checkboxLayout);
        LinearLayout innerLayout = new LinearLayout(getContext());
        CheckBox checkBox = new CheckBox(getContext());
        final EditText editText = new EditText(getContext());

        LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // EditText properties.
        editText.setLayoutParams(params);
        editText.setHint("Add Item");
        editText.setGravity(Gravity.TOP);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        editText.setTextSize(17);
        editText.setMaxLines(2);
        editText.setPadding(5, 5, 5, 5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editText.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        }

        checkBox.setLayoutParams(params);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editText.setPaintFlags(editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    editText.setPaintFlags( editText.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }
            }
        });

        // LinearLayout properties.
        innerLayout.setLayoutParams(params);
        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        innerLayout.addView(checkBox);
        innerLayout.addView(editText);

        // Finally add our view to main view.
        mLayout.addView(innerLayout);

        editTextList.add(editText);
        checkBoxList.add(checkBox);

        // Request keyboard focus "soft keyboard".
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void backPressed() {
        if (isDataChanged())
            showAlertDialog();
        else
            ((ChecklistDetailsActivity) getActivity()).setGoBack(true);
    }

    private boolean isDataChanged() {
        if (editTextList.size() != stringList.size() || (!titleView.getText().toString().equals(title))
                        && !titleView.getText().toString().equals(""))
            return true;

        for (int i = 0; i < editTextList.size(); i++) {
            if (!editTextList.get(i).getText().toString().equals(stringList.get(i)))
                return true;
            if (booleanList.get(i) ^ checkBoxList.get(i).isChecked())
                return true;
        }
        return false;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save changes");
        builder.setMessage("Do you want to save these changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save confirmed.
                dialog.dismiss();
                updateChecklist();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save denied.
                dialog.dismiss();
                ((ChecklistDetailsActivity) getActivity()).setGoBack(true);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateChecklist() {
        List<ListsModel> newList = new ArrayList<>();

        // Add new data to ArrayList.
        for (int i = 0; i < editTextList.size(); i++) {
            EditText editText = editTextList.get(i);
            CheckBox checkBox = checkBoxList.get(i);
            String content = editText.getText().toString();
            String isChecked = String.valueOf(checkBox.isChecked());
            if (content.equals("")) continue;
            newList.add(new ListsModel(content, isChecked));
        }

        // update our data.
        db.deleteItem(id);
        db.insertData(titleView.getText().toString(), newList);

        // Start done animation.
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new FragmentAnimation()).commit();
    }
}
