package com.example.plannit;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;


public class PlannitFragment extends Fragment {

    private String lastUsed, today;
    ImageView plannitPicture;
    TextView lblStreak, lblUpgrade;
    private int streak;

    public PlannitFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        lastUsed = activity.getLastUsed();
        streak = activity.getStreak();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        today = dateFormat.format(currentDate);

        System.out.println(lastUsed);
        System.out.println(today);
        System.out.println(streak);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plannit, container, false);

        lblStreak = rootView.findViewById(R.id.lblStreak);
        lblUpgrade = rootView.findViewById(R.id.lblUpgrade);
        plannitPicture = rootView.findViewById(R.id.plannitPicture);

        int streakMultiple = streak/5;

        String streakText = (streak+"\uD83D\uDD25");
        String upgradeText = String.valueOf(calculateNextUpgrade());

        lblStreak.setText(streakText);
        lblUpgrade.setText(upgradeText);
        setPlannitPicture(streakMultiple);

        return rootView;
    }

    public int calculateNextUpgrade() {

        return 5 - (streak % 5);
    }

    public void setPlannitPicture(int num) {

        Drawable drawable;

        switch (num) {

            case 0:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_1);
                break;
            case 1:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_2);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_3);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_4);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_5);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_6);
                break;
            default:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.plannit_7);
                break;

        }

        plannitPicture.setImageDrawable(drawable);
    }
}