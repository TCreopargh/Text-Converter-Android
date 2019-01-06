package xyz.tcreopargh.textconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView.BufferType;
import androidx.appcompat.app.AppCompatActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import java.util.Objects;

public class TextEditActivity extends AppCompatActivity {

    public static String text = "";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.editor);
        editText.setText(text, BufferType.EDITABLE);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editText.setHorizontallyScrolling(!sharedPreferences.getBoolean("doMultiLine", true));
        if (sharedPreferences.getBoolean("doUseMonospaced", false)) {
            editText.setTextAppearance(R.style.MyMonospace);
        } else {
            editText.setTextAppearance(R.style.MyRegular);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        new LovelyStandardDialog(this)
                .setIcon(R.drawable.baselinehelpoutline_white)
                .setTopColorRes(R.color.settingsGrey)
                .setTitle("退出确认")
                .setMessage("是否保存所有的更改?")
                .setNegativeButton("取消", v -> {})
                .setPositiveButtonColorRes(R.color.colorAccent)
                .setPositiveButton(
                        "保存",
                        v -> {
                            MainActivity.returnText = editText.getText().toString();
                            finish();
                        })
                .setNeutralButton("放弃更改", v -> finish())
                .create()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
