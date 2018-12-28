package xyz.tcreopargh.textconverter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import es.dmoral.toasty.Toasty;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private MyListener myListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myListener = (MyListener) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                boolean whether = Objects.requireNonNull(data).getBooleanExtra("easter_egg", false);
                Intent intent = new Intent();
                intent.putExtra("easter_egg1", whether);
                Objects.requireNonNull(getActivity()).setResult(RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("settings");
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        Preference resetSettings = findPreference("resetSettings");
        Preference about = findPreference("about");
        resetSettings.setOnPreferenceClickListener(
                preference -> {
                    LovelyStandardDialog lovelyStandardDialog1 =
                            new LovelyStandardDialog(
                                    getActivity(), LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                    lovelyStandardDialog1
                            .setTitle("重置设置")
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_settings_backup_restore_white_48dp)
                            .setMessage("是否要重置所有设置？此操作不可撤销。")
                            .setButtonsColorRes(R.color.colorAccent)
                            .setPositiveButton(
                                    "确定",
                                    v -> {
                                        SharedPreferences sharedPreferences1 =
                                                Objects.requireNonNull(getContext())
                                                        .getSharedPreferences(
                                                                "settings", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                                        editor.clear();
                                        boolean result = editor.commit();
                                        if (result) {
                                            myListener.sendContent(true);
                                            Toasty.success(
                                                            Objects.requireNonNull(getActivity()),
                                                            "重置成功！",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            Toasty.error(
                                                            Objects.requireNonNull(getActivity()),
                                                            "重置失败！",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    })
                            .setNegativeButton("取消", v -> lovelyStandardDialog1.dismiss())
                            .create()
                            .show();
                    return true;
                });
        about.setOnPreferenceClickListener(
                preference -> {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, 0);
                    return true;
                });
    }

    public interface MyListener {
        void sendContent(boolean isSettingsReset);
    }
}
