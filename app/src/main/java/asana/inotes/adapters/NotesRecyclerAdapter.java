package asana.inotes.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import asana.inotes.R;
import asana.inotes.activities.NoteDetailsActivity;
import asana.inotes.database.NotesDbHelper;
import asana.inotes.model.NotesModel;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.PlaceHolder> {

    private List<NotesModel> dataList;
    private int resourceID;
    private Context mContext;
    public NotesRecyclerAdapter(List<NotesModel> dataList, int resourceID, Context mContext) {
        this.dataList = dataList;
        this.resourceID = resourceID;
        this.mContext = mContext;
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
        NotesModel model = dataList.get(position);
        placeHolder.titleView.setText(model.getTitle());
        placeHolder.contentView.setText(model.getContent());
        placeHolder.dateView.setText(model.getDate());
        placeHolder.id = model.getId();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void removeItem(final int position, final long id) {
        // Create alert dialog to insure the deletion order.
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you want to delete this note?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Deletion confirmed.
                dataList.remove(position);
                notifyItemRemoved(position);
                new NotesDbHelper(mContext).deleteItem(id);
                Toast.makeText(mContext, "Note deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
        // Place holder for single row views.
        TextView titleView;
        TextView contentView;
        TextView dateView;
        CardView viewForeground;
        public long id;

        PlaceHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            // Initialize views with id's here.
            titleView = itemView.findViewById(R.id.row_title);
            contentView = itemView.findViewById(R.id.row_content);
            dateView = itemView.findViewById(R.id.row_date);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            viewForeground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start details activity with extras.
                    Intent intent = new Intent(context, NoteDetailsActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("title", titleView.getText().toString());
                    intent.putExtra("content", contentView.getText().toString());
                    context.startActivity(intent);
                }
            });
            viewForeground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeItem(getAdapterPosition(), id);
                    return true;
                }
            });
        }
    }
}
