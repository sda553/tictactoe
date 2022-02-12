package ru.sbertest.tictactoe.common;

public enum CellType {
    EMPTY(0), TIC(1), TAC(2) ;

    CellType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private final int value;
}
