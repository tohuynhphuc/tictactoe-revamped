package com.phuc.tictactoe.online.sust.protocol;

public interface Protocol {

    public int getSize();

    public boolean isValid(String raw);

    public void decode(String raw);

    @Override
    public String toString();

}
