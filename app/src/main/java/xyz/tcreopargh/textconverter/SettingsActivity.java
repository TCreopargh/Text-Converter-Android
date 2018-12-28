package xyz.tcreopargh.textconverter;

import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Objects;
import xyz.tcreopargh.textconverter.SettingsFragment.MyListener;

public class SettingsActivity extends AppCompatActivity implements MyListener {
    boolean settingsReset = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (settingsReset) {
            MainActivity.mainActivity.finish();
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                boolean whether = Objects.requireNonNull(data).getBooleanExtra("easter_egg", false);
                Intent intent = new Intent();
                intent.putExtra("easter_egg1", whether);
                setResult(RESULT_OK,intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_OK);
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFragment, settingsFragment)
                .commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void sendContent(boolean isSettingsReset) {
        settingsReset = isSettingsReset;
    }
}
