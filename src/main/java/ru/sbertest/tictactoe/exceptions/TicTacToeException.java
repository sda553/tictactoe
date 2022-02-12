package ru.sbertest.tictactoe.exceptions;

import lombok.Getter;

public class TicTacToeException extends Exception {

    @Getter
    private String gameMessage;

    public TicTacToeException(String message) {
        gameMessage = message;
    }
}
