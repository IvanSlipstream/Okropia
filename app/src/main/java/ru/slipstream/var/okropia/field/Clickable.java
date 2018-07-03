package ru.slipstream.var.okropia.field;

import ru.slipstream.var.okropia.mechanics.Clicker;

public interface Clickable {

    void onSelect(Clicker clicker, Clicker.CommandState commandState);

}
