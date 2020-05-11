package gghost.nerdlauncher;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class SingleFragmentActivity extends AppCompatActivity {

    private static final String SINGLE_FRAGMENT_TAG = "SINGLE_FRAGMENT_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        if (getSupportFragmentManager().findFragmentByTag(SINGLE_FRAGMENT_TAG) == null) {
            Fragment f = createFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, f, SINGLE_FRAGMENT_TAG)
                    .commit();
        }

    }

    protected Fragment createFragment() {
        Fragment f = new Fragment();
        return f;
    }
}
