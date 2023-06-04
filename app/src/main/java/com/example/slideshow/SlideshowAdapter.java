package com.example.slideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class SlideshowAdapter extends PagerAdapter {
    private List<Disease> diseases;
    private LayoutInflater inflater;

    public SlideshowAdapter(List<Disease> diseases, Context context) {
        this.diseases = diseases;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return diseases.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        TextView nameTextView = itemView.findViewById(R.id.nameTextView);
        TextView descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

        Disease disease = diseases.get(position);

        imageView.setImageResource(disease.getImageResId());
        nameTextView.setText(disease.getName());
        descriptionTextView.setText(disease.getDescription());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
