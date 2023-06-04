package com.example.slideshow;

import java.util.List;

public class Disease {
    private String name;
    private String description;
    private int imageResId;
    private List<String> correctionExercises;

    public Disease(String name, String description, int imageResId, List<String> correctionExercises) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
        this.correctionExercises = correctionExercises;
    }
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public List<String> getCorrectionExercises() {
        return correctionExercises;
    }
}
