package com.example.cm.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.cm.R;

public class OnboardingAdapter extends PagerAdapter {

    private final Context context;
    int[] images = {
            R.drawable.ic_conversation,
            R.drawable.ic_winke_profil,
            R.drawable.ic_having_fun,
            R.drawable.ic_my_location
    };
    int[] headings = {
            R.string.start_title,
            R.string.profile_title,
            R.string.meet_title,
            R.string.location_title
    };
    int[] descs = {
            R.string.start_header,
            R.string.profile_header,
            R.string.meet_header,
            R.string.location_header
    };

    public OnboardingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_onboarding, container, false);


        ImageView slideTitleImage = view.findViewById(R.id.onboarding_image);
        TextView slideTitle = view.findViewById(R.id.onboarding_title);
        TextView slideDesc = view.findViewById(R.id.onboarding_desc);

        slideTitleImage.setImageResource(images[position]);
        slideTitle.setText(headings[position]);
        slideDesc.setText(descs[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);
    }
}