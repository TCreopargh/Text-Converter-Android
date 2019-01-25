package xyz.tcreopargh.textconverter;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomRegexActivity extends AppCompatActivity {

    List<CustomRegex> regexList = new ArrayList<>();

    SwipeMenuRecyclerView recyclerView;

    RegexAdapter adapter;

    Context context;

    View container;

    boolean selectionMode = false;

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

        context = this;
        container = findViewById(R.id.custom_regex_context);

        setContentView(R.layout.activity_custom_regex);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        selectionMode = getIntent().getBooleanExtra("selectionMode", false);
        if (selectionMode) {
            setTitle(R.string.pick_regex);
        } else {
            setTitle(R.string.title_activity_custom_regex);
        }

        initList();
        recyclerView = findViewById(R.id.regexList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RegexAdapter(regexList);
        /*
                adapter.setOnItemClickListener(
                        (view, position) -> {
                            showCustomDialog(false, position);
                        });
        */

        SwipeMenuCreator swipeMenuCreator =
                (leftMenu, rightMenu, position) -> {
                    SwipeMenuItem delete =
                            new SwipeMenuItem(context)
                                    .setImage(R.drawable.ic_delete_black_24dp)
                                    .setHeight(MATCH_PARENT)
                                    .setWidth(200)
                                    .setBackgroundColorResource(R.color.colorRed);
                    leftMenu.addMenuItem(delete);
                    SwipeMenuItem edit =
                            new SwipeMenuItem(context)
                                    .setImage(R.drawable.ic_mode_edit_white_24dp)
                                    .setHeight(MATCH_PARENT)
                                    .setWidth(200)
                                    .setBackgroundColorResource(R.color.safeGreen);
                    leftMenu.addMenuItem(edit);
                };
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        recyclerView.setSwipeMenuItemClickListener(
                (menuBridge, position) -> {
                    menuBridge.closeMenu();
                    int direction = menuBridge.getDirection();
                    if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION
                            && menuBridge.getPosition() == 0) {
                        CustomRegex temp = regexList.get(position);
                        regexList.remove(position);
                        adapter.notifyItemRemoved(position);
                        refresh();
                        Snacky.builder()
                                .setActivity(CustomRegexActivity.this)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setText(R.string.delected)
                                .setActionText(R.string.undo)
                                .setActionClickListener(
                                        v -> {
                                            regexList.add(position, temp);
                                            adapter.notifyItemInserted(position);
                                            refresh();
                                        })
                                .success()
                                .show();
                    } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION
                            && menuBridge.getPosition() == 1) {
                        showCustomDialog(false, position);
                    }
                });
        recyclerView.setSwipeItemClickListener(
                (itemView, position) -> {
                    if (selectionMode) {
                        Intent intent = new Intent();
                        intent.putExtra("regexValue", regexList.get(position).getRegex());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        showCustomDialog(false, position);
                    }
                });
        recyclerView.setAdapter(adapter);
        // recyclerView.setItemViewSwipeEnabled(true);
        /*
        recyclerView.setOnItemMoveListener(
                new OnItemMoveListener() {
                    @Override
                    public boolean onItemMove(ViewHolder srcHolder, ViewHolder targetHolder) {
                        int fromPosition = srcHolder.getAdapterPosition();
                        int toPosition = targetHolder.getAdapterPosition();
                        Collections.swap(regexList, fromPosition, toPosition);
                        adapter.notifyItemMoved(fromPosition, toPosition);
                        return true;
                    }

                    @Override
                    public void onItemDismiss(ViewHolder viewHolder) {
                        int position = viewHolder.getAdapterPosition();
                        CustomRegex temp = regexList.get(position);
                        regexList.remove(position);
                        adapter.notifyItemRemoved(position);
                        Snacky.builder()
                                .setActivity(CustomRegexActivity.this)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setText("已删除")
                                .setActionText(R.string.undo)
                                .setActionClickListener(
                                        v -> {
                                            regexList.add(position, temp);
                                            adapter.notifyItemInserted(position);
                                        })
                                .success()
                                .show();
                    }
                });
                */

        FloatingActionButton fab = findViewById(R.id.addRegex);
        fab.setOnClickListener(view -> showCustomDialog(true, 0));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void initList() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        int size = sharedPreferences.getInt(CustomRegex.SIZE_KEY, 0);
        for (int i = 0; i < size; i++) {
            String label = sharedPreferences.getString(CustomRegex.LABEL_KEY + i, "");
            String regex = sharedPreferences.getString(CustomRegex.REGEX_KEY + i, "");
            CustomRegex customRegex = new CustomRegex(label, regex);
            regexList.add(customRegex);
        }
    }

    private void showCustomDialog(boolean doAddRegex, int position) {
        String title = doAddRegex ? getString(R.string.new_regex) : getString(R.string.edit_regex);
        final LovelyCustomDialog dialog = new LovelyCustomDialog(CustomRegexActivity.this);
        LayoutInflater layoutInflater = LayoutInflater.from(CustomRegexActivity.this);
        @SuppressLint("InflateParams")
        View dialogView = layoutInflater.inflate(R.layout.add_regex_dialog, null);
        final EditText regexValueBox = dialogView.findViewById(R.id.regexText);
        final EditText regexLabelBox = dialogView.findViewById(R.id.titleText);
        if (!doAddRegex) {
            regexValueBox.setText(regexList.get(position).getRegex(), BufferType.EDITABLE);
            regexLabelBox.setText(regexList.get(position).getLabel(), BufferType.EDITABLE);
        }

        dialog.setTitle(title)
                .setView(dialogView)
                .setTopColorRes(R.color.colorAccent)
                .setIcon(R.drawable.ic_code_white_48dp)
                .setListener(
                        R.id.confirmAddRegex,
                        v -> {
                            if (doAddRegex) {
                                String label = regexLabelBox.getText().toString();
                                String value = regexValueBox.getText().toString();
                                SharedPreferences sharedPreferences =
                                        getSharedPreferences("settings", MODE_PRIVATE);
                                int size = sharedPreferences.getInt(CustomRegex.SIZE_KEY, 0);
                                Editor editor = sharedPreferences.edit();
                                editor.putString(CustomRegex.REGEX_KEY + size, value)
                                        .putString(CustomRegex.LABEL_KEY + size, label)
                                        .putInt(CustomRegex.SIZE_KEY, size + 1)
                                        .apply();
                                regexList.add(new CustomRegex(label, value));
                                adapter.notifyItemChanged(size);
                                Toasty.success(
                                                CustomRegexActivity.this,
                                                R.string.add_success,
                                                Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                String label = regexLabelBox.getText().toString();
                                String value = regexValueBox.getText().toString();
                                SharedPreferences sharedPreferences =
                                        getSharedPreferences("settings", MODE_PRIVATE);
                                Editor editor = sharedPreferences.edit();
                                editor.putString(CustomRegex.REGEX_KEY + position, value)
                                        .putString(CustomRegex.LABEL_KEY + position, label)
                                        .apply();
                                regexList.set(position, new CustomRegex(label, value));
                                adapter.notifyItemChanged(position);
                                Toasty.success(
                                                CustomRegexActivity.this,
                                                R.string.edit_success,
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                            dialog.dismiss();
                        })
                .setListener(R.id.cancelAddRegex, v -> dialog.dismiss())
            .setListener(R.id.copyRegex, v -> {
                ClipboardManager clipboardManager =
                    (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData =
                    ClipData.newPlainText(
                        "TextConverter_Custom_Regex",
                        regexValueBox.getText().toString());
                clipboardManager.setPrimaryClip(mClipData);
                Toasty.success(
                    CustomRegexActivity.this,
                    R.string.clipboard_success,
                    Toast.LENGTH_SHORT,
                    true)
                    .show();
                dialog.dismiss();
            })
                .create()
                .show();
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

    private void refresh() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < regexList.size(); i++) {
            editor.remove(CustomRegex.REGEX_KEY + i).remove(CustomRegex.LABEL_KEY + i);
        }
        editor.remove(CustomRegex.SIZE_KEY);
        for (int i = 0; i < regexList.size(); i++) {
            editor.putString(CustomRegex.REGEX_KEY + i, regexList.get(i).getRegex());
            editor.putString(CustomRegex.LABEL_KEY + i, regexList.get(i).getLabel());
            editor.putInt(CustomRegex.SIZE_KEY, regexList.size());
        }
        editor.apply();
    }
}
