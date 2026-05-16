package com.nomnom.recipes.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CategoryResponse {

    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() { return categories; }
}