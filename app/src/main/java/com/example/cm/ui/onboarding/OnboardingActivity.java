package com.example.cm.ui.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cm.R;
import com.example.cm.ui.adapters.OnboardingAdapter;
import com.example.cm.ui.auth.LoginActivity;
import com.example.cm.ui.auth.RegisterActivity;

public class OnboardingActivity extends AppCompatActivity {

    private LinearLayout dotsLayout;
    private Button registerBtn;
    private int currentPos;

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
                registerBtn.setAnimation(btnAnimation);
                registerBtn.setVisibility(View.VISIBLE);
            } else {
                registerBtn.setVisibility(View.INVISIBLE);
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
        setContentView(R.layout.activity_onboarding);

        ViewPager viewPager = findViewById(R.id.onboarding_viewpager);
        dotsLayout = findViewById(R.id.layout_onboarding_indicator);
        registerBtn = findViewById(R.id.onboarding_btn_register);

        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(this);

        viewPager.setAdapter(onboardingAdapter);
        addDots(0);
        viewPager.addOnPageChangeListener(onPageChangeListener);
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
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);

            dotsLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.orange500));
    }

    @Override
    public void onBackPressed() {
    }
}