package asana.inotes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import asana.inotes.R;
import asana.inotes.fragments.FragmentNoteDetails;
import asana.inotes.fragments.IonBackPressInFrag;

public class NoteDetailsActivity extends AppCompatActivity {

    boolean goBack;
    public IonBackPressInFrag backPressInFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getExtras().getString("title"));
        goBack = false;

        FragmentNoteDetails fragment = new FragmentNoteDetails();

        // Pass extras to the fragment.
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        bundle.putLong("id", intent.getLongExtra("id", -1));
        bundle.putString("title", intent.getStringExtra("title"));
        bundle.putString("content", intent.getStringExtra("content"));
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).commit();
    }

    public void setGoBack(boolean goBack) {
        this.goBack = goBack;
        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!goBack)
            backPressInFrag.backPressed();
        else
            super.onBackPressed();
    }
}
