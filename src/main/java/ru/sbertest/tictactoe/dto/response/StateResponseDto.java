package ru.sbertest.tictactoe.dto.response;

import lombok.Data;
import ru.sbertest.tictactoe.common.CellType;
import ru.sbertest.tictactoe.common.PlayerType;
import ru.sbertest.tictactoe.common.ResultState;
import ru.sbertest.tictactoe.model.Game;

import static ru.sbertest.tictactoe.common.Utils.intToState;

@Data
public class StateResponseDto {

    private CellType[][] state;

    private ResultState resultState;

    private PlayerType winner;

    public StateResponseDto(Game game) {

        this.state = intToState(game.getState().getState());
        this.resultState = game.getResult();
        this.winner = game.getWinner();
    }
}
