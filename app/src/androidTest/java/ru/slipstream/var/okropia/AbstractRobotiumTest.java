package ru.slipstream.var.okropia;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public abstract class AbstractRobotiumTest extends ActivityInstrumentationTestCase2 {

    protected Solo solo;
    protected static final String X_WRONG_ACTIVITY = "Wrong activity";

    public AbstractRobotiumTest(Class activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
