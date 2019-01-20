package unipd.se18.ocrcamera;


import android.app.SearchManager;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityUITest {
    //third param is set to false so the activity is not launched automatically, it will be
    //launched with an Action Search intent
    @Rule
    public ActivityTestRule<SearchResultsActivity> mActivityRule
            = new ActivityTestRule<>(SearchResultsActivity.class, false, false);

    @Before
    public void startActivityWithActionSearch() {
        Intent i = new Intent(Intent.ACTION_SEARCH);
        i.putExtra(SearchManager.QUERY, "");
        mActivityRule.launchActivity(i);
    }

    @Test
    public void performSearch() {

        //test ingredient found
        onView(withId(R.id.ingredients_auto_complete_text_view))
                .perform(click())
                .perform(replaceText("acetone"));
        onView(withId(R.id.auto_complete_text_view_button))
                .perform(click());
        onView(withId(R.id.ingredients_auto_complete_text_view))
                .perform(click())
                .perform(replaceText("end"));

        onData(anything())
                .inAdapterView(withId(R.id.ingredients_list))
                .atPosition(0)
                .onChildView(withId(R.id.inci_name_view))
                .check(matches(withText("ACETONE")));

        //test ingredient not found
        onView(withId(R.id.ingredients_auto_complete_text_view))
                .perform(click())
                .perform(replaceText("non-existent ingredient"));
        onView(withId(R.id.auto_complete_text_view_button))
                .perform(click());
        onView(withId(R.id.ingredients_auto_complete_text_view))
                .perform(click())
                .perform(replaceText("end"));

        onView(withId(R.id.message_text_view)).check(matches(withSubstring("Nothing found")));
    }

    @Test
    public void onEmptyResultHint() {
        //test clickable hint visibility in case the research produced empty result
        onView(withId(R.id.ingredients_auto_complete_text_view))
                .perform(click())
                .perform(replaceText("acetoni"));
        onView(withId(R.id.auto_complete_text_view_button))
                .perform(click());
        onView(withId(R.id.message_text_view)).check(matches(withSubstring("ACETONE")));
    }
}
