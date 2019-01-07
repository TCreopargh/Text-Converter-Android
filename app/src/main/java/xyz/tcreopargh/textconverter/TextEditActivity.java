package xyz.tcreopargh.textconverter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import java.util.Objects;
import ren.qinc.edit.PerformEdit;

public class TextEditActivity extends AppCompatActivity {

    public static String text = "";

    private EditText editText;
    private PerformEdit performEdit;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_mode_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.requestApplyInsets(mChildView);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_edit);
        androidx.appcompat.widget.Toolbar myToolBar = findViewById(R.id.editModeToolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.editor);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        editText.setHorizontallyScrolling(!sharedPreferences.getBoolean("doMultiLine", true));
        if (sharedPreferences.getBoolean("doUseMonospaced", false)) {
            editText.setTextAppearance(R.style.MyMonospace);
        } else {
            editText.setTextAppearance(R.style.MyRegular);
        }
        performEdit = new PerformEdit(editText);
        performEdit.setDefaultText(text);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.editUndo:
                performEdit.undo();
                break;
            case R.id.editRedo:
                performEdit.redo();
                break;
            case R.id.editHelp:
                new LovelyInfoDialog(this)
                        .setTitle("编辑模式")
                        .setTopColorRes(R.color.safeGreen)
                        .setIcon(R.drawable.baselinehelpoutline_white)
                        .setMessage(
                                "编辑模式是专为长文本编辑打造的界面，在这里你可以屏蔽一切干扰，专心编辑文本。\n"
                                        + "本模式带有撤销和反撤销功能，如需回退之前的界面轻触返回键即可，系统会询问您是否需要保存更改。\n"
                                        + "设置中的等宽字体和自动换行设置在这里仍然有效。")
                        .setConfirmButtonText("确定")
                        .create()
                        .show();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        String verifiedText = editText.getText().toString();
        if (verifiedText.equals(text)) {
            finish();
        } else {
            new LovelyStandardDialog(this)
                    .setIcon(R.drawable.ic_save_white_48dp)
                    .setTopColorRes(R.color.settingsGrey)
                    .setTitle("退出确认")
                    .setMessage("是否保存所有的更改?")
                    .setNegativeButton("取消", v -> {})
                    .setPositiveButtonColorRes(R.color.colorAccent)
                    .setPositiveButton(
                            "保存",
                            v -> {
                                MainActivity.returnText = verifiedText;
                                finish();
                            })
                    .setNeutralButton("放弃更改", v -> finish())
                    .create()
                    .show();
        }
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
