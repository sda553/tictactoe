package ru.sbertest.tictactoe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.sbertest.tictactoe.common.PlayerType;

@Data
public class NewGameRequestDto {
    @JsonProperty("first_move")
    PlayerType firstMove;
}
