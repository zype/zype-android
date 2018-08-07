package com.zype.android.ui.launch;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.zype.android.R;
import com.zype.android.ui.main.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.ClassRule;
import org.junit.BeforeClass;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import java.io.*;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FastlaneTest {

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void beforeAll() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

    @Test
    public void fastlaneTest() {

        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.list_playlist),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(0);

        try {
            Thread.sleep(10000);
            Screengrab.screenshot("01HomeScreen");
            relativeLayout.perform(click());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(10000);
            Screengrab.screenshot("02Playlist");
            pressBack();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);

            ViewInteraction tabView = onView(withText("SETTINGS"));
            tabView.perform(click());

            ViewInteraction viewPager = onView(
                    allOf(withId(R.id.pager),
                            childAtPosition(
                                    allOf(withId(R.id.root_view),
                                            childAtPosition(
                                                    withClassName(is("android.support.design.widget.CoordinatorLayout")),
                                                    1)),
                                    0),
                            isDisplayed()));
            viewPager.perform(swipeLeft());

            Screengrab.screenshot("03SettingsScreen");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);

            ViewInteraction appCompatButton = onView(
                    allOf(withId(R.id.sign_in_button), withText("Sign in"),
                            childAtPosition(
                                    withParent(withId(R.id.pager)),
                                    1),
                            isDisplayed()));
            appCompatButton.perform(click());

            Thread.sleep(2000);

            Screengrab.screenshot("04SignInScreen");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
