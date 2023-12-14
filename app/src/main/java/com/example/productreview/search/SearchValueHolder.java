package com.example.productreview.search;

public class SearchValueHolder {
    String category,id,tag;

    SearchValueHolder(){}

    public SearchValueHolder(String category, String id, String tag) {
        this.category = category;
        this.id = id;
        this.tag = tag;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }
}
