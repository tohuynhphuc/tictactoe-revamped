package com.phuc.tictactoe.online.mumt.protocol;

public interface Protocol {

    public int getSize();

    public boolean isValid(String raw);

    public void decode(String raw);

    @Override
    public String toString();

}
