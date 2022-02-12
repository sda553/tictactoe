package ru.sbertest.tictactoe.common;

public class Utils {
    public static final int[] powsOfThree = {1,3,9,27,81,243,729,2187,6561};
    public static final CellType[] cellTypes = {CellType.EMPTY, CellType.TIC, CellType.TAC};

    public static int stateToInt(CellType[][] state) {
        int result = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                result+= powsOfThree[row*3+col]*state[row][col].getValue();
            }
        }
        return result;
    }

    public static CellType[][] intToState(int intState) {
        CellType[][] state = new CellType[3][3];
        int remainer = intState;
        for (int row = 2; row >=0; row--) {
            for (int col = 2; col >= 0; col--) {
                int divider =  remainer / powsOfThree[row*3+col];
                remainer = remainer % powsOfThree[row*3+col];
                state[row][col]= cellTypes[divider];
            }
        }
        return state;
    }

}
