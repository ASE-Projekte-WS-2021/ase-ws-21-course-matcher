package com.example.cm.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.cm.R;
import com.example.cm.databinding.ActivityOnboardingBinding;
import com.example.cm.ui.adapters.OnboardingAdapter;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.auth.RegisterActivity;

public class OnboardingActivity extends AppCompatActivity {

    private static final int FINAL_PAGE = 3;
    private ActivityOnboardingBinding binding;
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            if (position == FINAL_PAGE) {
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
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUI();
        initListeners();
    }

    private void initUI() {
        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(this);
        binding.onboardingViewpager.setAdapter(onboardingAdapter);
        binding.onboardingViewpager.addOnPageChangeListener(onPageChangeListener);
        addDots(0);
    }

    private void initListeners() {
        binding.onboardingToLoginBtn.setOnClickListener(v -> {
            onLoginClicked();
        });
        binding.onboardingBtnRegister.setOnClickListener(v -> {
            onRegisterClicked();
        });
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

    public void onLoginClicked() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onRegisterClicked() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }
}