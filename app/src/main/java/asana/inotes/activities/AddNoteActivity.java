package asana.inotes.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import asana.inotes.R;
import asana.inotes.fragments.FragmentAddNote;
import asana.inotes.fragments.IonBackPressInFrag;

public class AddNoteActivity extends AppCompatActivity {

    boolean goBack;
    public IonBackPressInFrag backPressInFrag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        goBack = false;

        // ActionBar title and back button.
        getSupportActionBar().setTitle("Add note");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, new FragmentAddNote()).commit();
    }

    // will perform super.onBackPressed() from fragment if true.
    public void setGoBack(boolean goBack) {
        this.goBack = goBack;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (!goBack)
            // backPressed in fragment.
            backPressInFrag.backPressed();
        else
            // Go back.
            super.onBackPressed();
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
}
