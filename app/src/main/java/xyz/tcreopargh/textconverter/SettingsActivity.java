package xyz.tcreopargh.textconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import es.dmoral.toasty.Toasty;
import java.io.File;
import java.util.Objects;
import xyz.tcreopargh.textconverter.SettingsFragment.MyListener;

public class SettingsActivity extends AppCompatActivity implements MyListener {
    public static String path = "";
    public static int REQUESTCODE_WRITE = 2000;
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
                setResult(RESULT_OK, intent);
            } else if (requestCode == REQUESTCODE_WRITE) {
                path = Objects.requireNonNull(data).getStringExtra("path");
                SharedPreferences sharedPreferences =
                        getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("default_path", path);
                editor.apply();
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

    @Override
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

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 233:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getStoreLocation();
                } else {
                    Toasty.error(
                                    SettingsActivity.this,
                                    R.string.file_permission_denied,
                                    Toast.LENGTH_LONG,
                                    true)
                            .show();
                }
                break;
            default:
        }
    }

    public void reLoadFragView() {
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFragment, settingsFragment)
                .commit();
    }

    private void getStoreLocation() {
        String pathTemp =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/TextConverter";
        File destDir = new File(pathTemp);
        if (!destDir.exists()) {
            boolean doMkdirSuccess = destDir.mkdirs();
            if (!doMkdirSuccess) {
                Toasty.error(this, R.string.folder_create_failed, Toast.LENGTH_LONG, true).show();
                pathTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        SettingsActivity.path = "";
        new LFilePicker()
                .withActivity(this)
                .withBackgroundColor("#03a9f4")
                .withRequestCode(REQUESTCODE_WRITE)
                .withTitle(getString(R.string.pick_folder))
                .withChooseMode(false)
                .withStartPath(pathTemp)
                .withIconStyle(Constant.ICON_STYLE_YELLOW)
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }
}
