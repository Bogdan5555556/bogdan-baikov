package com.apitest.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CorrespondToPatternMatcher extends TypeSafeMatcher<String> {

    Pattern pattern;

    CorrespondToPatternMatcher(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Actual char sequence is not correspond to pattern:" + pattern.pattern());
    }

    @Override
    protected boolean matchesSafely(String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    public static CorrespondToPatternMatcher correspondToPattern(String pattern) {
        return new CorrespondToPatternMatcher(pattern);
    }
}
