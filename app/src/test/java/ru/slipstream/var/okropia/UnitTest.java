package ru.slipstream.var.okropia;

import android.graphics.PointF;

import org.junit.Test;

import ru.slipstream.var.okropia.field.FieldState;
import ru.slipstream.var.okropia.field.Location;

import static org.junit.Assert.assertEquals;

/**
 * Created by Slipstream-DESKTOP on 18.02.2018.
 */

public class UnitTest {

    @Test
    public void setPivot_FieldState() throws Exception {
        FieldState state = new FieldState();
        state.setPivot(new Location(0.05f, 0.4f));
        assertEquals( state.getPivot().x, FieldState.BORDER_SIZE, 0f);
        assertEquals(0.4f, state.getPivot().y, 0f);
        state.setPivot(new Location(0.4f, 2.3f));
        assertEquals(1 - FieldState.BORDER_SIZE, state.getPivot().y, 0f);
    }
}
