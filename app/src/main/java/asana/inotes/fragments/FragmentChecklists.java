package asana.inotes.fragments;

import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.List;

import asana.inotes.R;
import asana.inotes.adapters.ChecklistsRecyclerAdapter;
import asana.inotes.database.ChecklistsDbHelper;
import asana.inotes.model.ChecklistsModel;

public class FragmentChecklists extends Fragment {

    List<ChecklistsModel> dataList;
    ChecklistsDbHelper db;
    RecyclerView mRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        db = new ChecklistsDbHelper(getContext());
        dataList = db.getInsertedData();
        if (dataList.size() != 0)
            // If there is at least one checklist.
            v = inflater.inflate(R.layout.fragment_checklists, container, false);
        else
            // No lists inserted.
            v = inflater.inflate(R.layout.fragment_no_entries, container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (dataList.size() != 0) {
            mRecycler = view.findViewById(R.id.checklists_recyclerView);
            // Creating reverse linear layout manager.
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            // RecyclerView properties.
            mRecycler.setHasFixedSize(true);
            mRecycler.setLayoutManager(mLayoutManager);
            mRecycler.setItemAnimator(new DefaultItemAnimator());
            mRecycler.setAdapter(new ChecklistsRecyclerAdapter(dataList, R.layout.row_check_element, getContext()));
        } else {
            TextView noEtriesView = view.findViewById(R.id.noEntriesView);
            noEtriesView.setText("No checklists, you can add some with this button");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_checklist).setVisible(true);
        menu.findItem(R.id.action_notes).setVisible(false);
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
            case R.id.action_checklist:
                // Checklists button on top menu.
                Toast.makeText(getContext(), "Swipe right to go to notes", Toast.LENGTH_SHORT).show();
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
                    mRecycler.setAdapter(new ChecklistsRecyclerAdapter(dataList ,R.layout.row_check_element ,getContext()));
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                    searchBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchForString(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void searchForString(String text) {
        List<ChecklistsModel> searchList = db.searchData(text);
        mRecycler.setAdapter(new ChecklistsRecyclerAdapter(searchList, R.layout.row_check_element, getContext()));
    }
}
