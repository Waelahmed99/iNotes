package asana.inotes.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.mancj.materialsearchbar.MaterialSearchBar;
import java.util.List;

import asana.inotes.R;
import asana.inotes.adapters.NotesRecyclerAdapter;
import asana.inotes.database.NotesDbHelper;
import asana.inotes.model.NotesModel;

public class FragmentNotes extends Fragment {

    NotesDbHelper db;
    List<NotesModel> dataList;
    RecyclerView mRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        db = new NotesDbHelper(getContext());
        dataList = db.getInsertedData();
        if (dataList.size() != 0)
            // If there is at least one note.
            v = inflater.inflate(R.layout.fragment_notes, container, false);
        else
            // No notes inserted.
            v = inflater.inflate(R.layout.fragment_no_entries, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (dataList.size() != 0) {
            mRecycler = view.findViewById(R.id.notes_recyclerView);
            // Creating reverse linear layout manager.
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            // RecyclerView properties.
            mRecycler.setHasFixedSize(true);
            mRecycler.setLayoutManager(mLayoutManager);
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.setAdapter(new NotesRecyclerAdapter(dataList, R.layout.row_item, getContext()));
        } else {
            TextView noEntriesView = view.findViewById(R.id.noEntriesView);
            noEntriesView.setText("No notes, you can add some with this button");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_checklist).setVisible(false);
        menu.findItem(R.id.action_notes).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Search button on top menu.
                if (dataList.size() != 0) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    startSearchEvent();
                }
                return true;
            case R.id.action_notes:
                // Notes button on top menu.
                Toast.makeText(getContext(), "Swipe left to go to checklists", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startSearchEvent() {
        final MaterialSearchBar searchBar = getView().findViewById(R.id.searchbar);
        searchBar.setCardViewElevation(10);
        searchBar.setVisibility(View.VISIBLE);
        searchBar.enableSearch();

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    // Return to default adapter. search cancelled.
                    mRecycler.setAdapter(new NotesRecyclerAdapter(dataList ,R.layout.row_item ,getContext()));
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                // Start searching.
                searchForString(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void searchForString(String text) {
        List<NotesModel> searchList = db.searchData(text);
        mRecycler.swapAdapter(new NotesRecyclerAdapter(searchList, R.layout.row_item, getContext())
                , false);
    }
}
