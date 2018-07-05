package ru.slipstream.var.okropia;

import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import ru.slipstream.var.okropia.views.FieldView;

public class SurfaceTests extends AbstractRobotiumTest {

    private Solo solo;
    private static final String X_WRONG_ACTIVITY = "Wrong activity";

    public SurfaceTests() {
        super(MainActivity.class);
    }

    public void testChangeScale() throws Exception {
        solo.assertCurrentActivity(X_WRONG_ACTIVITY, MainActivity.class);
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        FieldView fieldView = activity.findViewById(R.id.fv_main);
        for (int i=0;i<50;i++){
            fieldView.setCurrentScale(i/10f);
            solo.sleep(500);
        }
    }

    public void testGestureScale() throws Exception {
        solo.assertCurrentActivity(X_WRONG_ACTIVITY, MainActivity.class);
        solo.pinchToZoom(new PointF(300, 300), new PointF(400, 400),
                new PointF(200, 200), new PointF(500, 500));
        solo.sleep(2000);
    }
}
