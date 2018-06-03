package ru.slipstream.var.okropia.mechanics;

import android.os.Bundle;

public interface CommandReceiver {
    boolean sendUserCommand(int commandId, Bundle parameters);
}
