package com.terminal.gclo.Fragments.NavigationFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.terminal.gclo.MainActivity;
import com.terminal.gclo.R;
import com.terminal.gclo.Utility.GlobalVariable;
import com.terminal.gclo.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private static final String TAG = "my_debug";


    FragmentSettingBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        binding = FragmentSettingBinding.bind(view);

        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle back press here
                // For example, you can navigate to another fragment or perform other actions
                // If you want to allow the default back button behavior, call isEnabled() before handling it

                // Uncomment the next line if you want to allow the default back button behavior
//                if (isEnabled()) {
//                    super.handleOnBackPressed();
//                }
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(GlobalVariable.SETTING_PREFS_NAME, Context.MODE_PRIVATE);
        loadCheckboxState(binding.switchAutoScroll, sharedPreferences);
        autoScrollTerminal(binding.switchAutoScroll, sharedPreferences);

        return view;
    }

    public static void saveCheckboxState(CompoundButton compoundButton, SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(GlobalVariable.AUTO_SCROLL_KEY, compoundButton.isChecked());
        editor.apply();
    }

    public static void loadCheckboxState(CompoundButton compoundButton, SharedPreferences sharedPreferences){
        boolean isToggleed = sharedPreferences.getBoolean(GlobalVariable.AUTO_SCROLL_KEY, false);
        compoundButton.setChecked(isToggleed);
    }

    public void autoScrollTerminal(CompoundButton compoundButton, SharedPreferences sharedPreferences) {
        binding.switchAutoScroll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveCheckboxState(compoundButton, sharedPreferences);
            Log.d(TAG, "autoScrollTerminal: " + compoundButton);
        });
    }
/*    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }*/
}
