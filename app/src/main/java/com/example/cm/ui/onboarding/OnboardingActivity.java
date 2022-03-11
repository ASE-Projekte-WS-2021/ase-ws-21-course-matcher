package com.example.cm.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.cm.R;
import com.example.cm.databinding.ActivityLoginBinding;
import com.example.cm.databinding.ActivityOnboardingBinding;
import com.example.cm.ui.adapters.OnboardingAdapter;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.auth.RegisterActivity;

public class OnboardingActivity extends AppCompatActivity {

    private int currentPos;
    private ActivityOnboardingBinding binding;

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            currentPos = position;
            if (position == 3) {
                Animation btnAnimation = AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.bottom_anim);
                binding.onboardingBtnRegister.setAnimation(btnAnimation);
                binding.onboardingBtnRegister.setVisibility(View.VISIBLE);
            } else {
                binding.onboardingBtnRegister.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(this);

        binding.onboardingViewpager.setAdapter(onboardingAdapter);
        addDots(0);
        binding.onboardingViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    public void skipOB(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void toRegister(View view) {
        if (currentPos == 3) {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }
    }

    private void addDots(int position) {
        TextView[] dots = new TextView[4];
        binding.layoutOnboardingIndicator.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            binding.layoutOnboardingIndicator.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.orange500));
    }
}