package asana.inotes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import asana.inotes.R;
import asana.inotes.activities.AddChecklistActivity;
import asana.inotes.activities.ChecklistDetailsActivity;
import asana.inotes.database.ChecklistsDbHelper;
import asana.inotes.model.ChecklistsModel;
import asana.inotes.model.ListsModel;

public class ChecklistsRecyclerAdapter extends RecyclerView.Adapter<ChecklistsRecyclerAdapter.PlaceHolder> {

    List<ChecklistsModel> dataList;
    int resourceID;
    Context mContext;
    PlaceHolder placeHolder;
    View view;
    public ChecklistsRecyclerAdapter(List<ChecklistsModel> list, int resource, Context context) {
        this.dataList = list;
        resourceID = resource;
        mContext = context;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(resourceID, viewGroup, false);
        return new PlaceHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder placeHolder, int position) {
        // put values for placeHolder here.
        ChecklistsModel model = dataList.get(position);
        this.placeHolder = placeHolder;
        placeHolder.id = model.getId();
        placeHolder.titleView.setText(model.getTitle());
        placeHolder.dateView.setText(model.getDate());
        placeHolder.updateLayoutUI();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void removeItem(final int position, final ChecklistsModel model) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to delete this list?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deletion confirmed.
                dataList.remove(position);
                new ChecklistsDbHelper(mContext).deleteItem(model.getId());
                notifyItemRemoved(position);
                dialog.dismiss();
                Toast.makeText(mContext, "Checklist deleted", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deletion denied.
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView dateView;
        View itemView;
        Context mContext;
        CardView viewForeground;
        boolean entered;
        long id;

        PlaceHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.itemView = itemView;
            this.mContext = context;
            entered = false;
            titleView = itemView.findViewById(R.id.row_check_title);
            dateView = itemView.findViewById(R.id.row_check_date);
            final Intent intent = new Intent(mContext, ChecklistDetailsActivity.class);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("id", id);
                    intent.putExtra("title", titleView.getText().toString());
                    mContext.startActivity(intent);
                }
            });
        }

        void updateLayoutUI() {
            ChecklistsDbHelper db = new ChecklistsDbHelper(mContext);
            List<ListsModel> listsModels = db.findListByID(id);
            LinearLayout mLayout = itemView.findViewById(R.id.rowLayout);

            if (entered) return;
            entered = true;
            int size = listsModels.size() > 2 ? 2 : listsModels.size();
            for (int i = 0; i < size; i++) {
                LinearLayout innerLayout = new LinearLayout(mContext);
                CheckBox checkBox = new CheckBox(mContext);
                TextView textView = new TextView(mContext);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                // TextView properties.
                textView.setLayoutParams(params);
                textView.setGravity(Gravity.TOP);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                }
                textView.setTextSize(15);
                textView.setMaxLines(2);
                textView.setTextColor(Color.parseColor("#191919"));

                // CheckBox properties.
                ListsModel model = listsModels.get(i);
                checkBox.setClickable(false);
                checkBox.setChecked(Boolean.parseBoolean(model.isChecked()));

                // Change checkbox box color with StateList.
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

                // Add line through text if checkbox is checked.
                if (checkBox.isChecked()) {
                    textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                }

                 /* Add "..." to last list item if there is more than 2 inside the checklist.
                 So the user can no that this list still has more element than shown
                 "Maximum two elements in RecyclerView" */
                if (i == 1 && listsModels.size() > 2)
                    textView.setText(listsModels.get(i).getContent().concat("..."));
                else
                    textView.setText(listsModels.get(i).getContent());

                // LinearLayout properties.
                params.setMargins(5, 5 ,5, 5);
                innerLayout.setLayoutParams(params);
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                innerLayout.addView(checkBox);
                innerLayout.addView(textView);

                // Finally add this view to our main view.
                mLayout.addView(innerLayout);
            }

            // On checklist hold. "Long click", delete element.
            final ChecklistsModel model = new ChecklistsModel(id, titleView.getText().toString(),
                    listsModels, dateView.getText().toString());
            viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeItem(getAdapterPosition(), model);
                    return true;
                }
            });
        }
    }

}
