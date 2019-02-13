package xyz.tcreopargh.textconverter;

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
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.mixiaoxiao.fastscroll.FastScrollDelegate;
import com.mixiaoxiao.fastscroll.FastScrollDelegate.IndicatorPopup;
import com.mixiaoxiao.fastscroll.FastScrollDelegate.OnFastScrollListener;
import com.mixiaoxiao.fastscroll.FastScrollScrollView;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import es.dmoral.toasty.Toasty;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import ren.qinc.edit.PerformEdit;

public class TextEditActivity extends AppCompatActivity {

    public static String[] charsets;
    public static String text = "";
    public static String currentCharset = "UTF-8";
    private EditText editText;
    private PerformEdit performEdit;
    private boolean doMultiLine = true;
    private boolean doMonospace = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem doMultiLineMenu = menu.findItem(R.id.menuMultiLine);
        doMultiLineMenu.setChecked(doMultiLine);
        MenuItem doMonospaceMenu = menu.findItem(R.id.menuMonospace);
        doMonospaceMenu.setChecked(doMonospace);
        return super.onPrepareOptionsMenu(menu);
    }

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

        doMultiLine = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("doMultiLine", true);
        doMonospace = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("doUseMonospaced", false);

        charsets = getResources().getStringArray(R.array.encodingValues);
        androidx.appcompat.widget.Toolbar myToolBar = findViewById(R.id.editModeToolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        editText = findViewById(R.id.editor);
        FastScrollScrollView scrollView = findViewById(R.id.scrollView);

        FastScrollDelegate delegate = scrollView.getFastScrollDelegate();
        delegate.initIndicatorPopup(
            new IndicatorPopup.Builder(delegate)
                .indicatorPopupColor(getColor(R.color.colorAccent))
                .indicatorTextSize(24)
                .build());
        delegate.setOnFastScrollListener(
            new OnFastScrollListener() {
                @Override
                public void onFastScrollStart(
                    View view, FastScrollDelegate fastScrollDelegate) {
                }

                @Override
                public void onFastScrolled(
                    View view,
                    FastScrollDelegate fastScrollDelegate,
                    int i,
                    int i1,
                    float v) {
                    delegate.setIndicatorText(String.valueOf((int) (v * 100)) + "%");
                }

                @Override
                public void onFastScrollEnd(View view, FastScrollDelegate fastScrollDelegate) {
                }
            });

        editText.setHorizontallyScrolling(!doMultiLine);
        if (doMonospace) {
            editText.setTextAppearance(R.style.MyMonospace);
        } else {
            editText.setTextAppearance(R.style.MyRegular);
        }
        performEdit = new PerformEdit(editText);
        performEdit.setDefaultText(text);
        setTitle(R.string.edit_mode);
    }

    @Override
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
                    .setTitle(getString(R.string.edit_mode))
                    .setTopColorRes(R.color.safeGreen)
                    .setIcon(R.drawable.baselinehelpoutline_white)
                    .setMessage(
                        getString(R.string.edit_mode_intro))
                    .setConfirmButtonText(R.string.confirm)
                    .create()
                    .show();
                break;
            case R.id.changeEncoding:
                Builder alertDialog = new Builder(TextEditActivity.this);
                alertDialog
                    .setTitle(getString(R.string.encoding_now) + currentCharset)
                    .setIcon(R.mipmap.ic_launcher)
                    .setItems(charsets, (dialog12, which) -> setCharset(charsets[which]))
                    .create()
                    .show();
                break;
            case R.id.menuMultiLine:
                item.setChecked(!item.isChecked());
                doMultiLine = item.isChecked();
                editText.setHorizontallyScrolling(!doMultiLine);
                break;
            case R.id.menuMonospace:
                item.setChecked(!item.isChecked());
                doMonospace = item.isChecked();
                editText.setTextAppearance(doMonospace ? R.style.MyMonospace : R.style.MyRegular);
                break;
            default:
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
                .setTitle(getString(R.string.confirm_to_exit))
                .setMessage(getString(R.string.save_changes_whether))
                .setNegativeButton(getString(R.string.cancel_zh), v -> {
                })
                .setPositiveButtonColorRes(R.color.colorAccent)
                .setPositiveButton(
                    getString(R.string.save),
                    v -> {
                        MainActivity.returnText = verifiedText;
                        // MainActivity.encoding = currentCharset;
                        finish();
                    })
                .setNeutralButton(getString(R.string.abandon_changes), v -> finish())
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

    private void setCharset(String newCharset) {
        String temp = currentCharset;
        try {
            editText.setText(
                new String(editText.getText().toString().getBytes(currentCharset), newCharset));
            currentCharset = newCharset;
        } catch (UnsupportedEncodingException e) {
            Toasty.error(this, getString(R.string.encoding_not_supported), Toast.LENGTH_LONG).show();
            currentCharset = temp;
        }
    }
}
