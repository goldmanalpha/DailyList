package com.goldmanalpha.dailydo.Main;

import com.goldmanalpha.dailydo.model.SimpleLookup;

import java.util.regex.Pattern;

import lombok.Getter;

public class SearchSupport {

    public static final String BLANK = "";

    private String searchString = BLANK;

    @Getter
    private boolean isSearchMode;
    private Pattern pattern;

    public void setCategory(int categoryId) {
        isSearchMode = categoryId == SimpleLookup.ALL_ID;
    }

    public void setSearchString(String s) {
        searchString = s.trim();
        pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
    }

    public boolean isMatch(String s) {
        if (searchString.equals(BLANK) || !isSearchMode || s.length() < 2) {
            return true;
        }

        return pattern.matcher(s).find();
    }
}
