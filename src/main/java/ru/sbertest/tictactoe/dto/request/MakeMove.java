package ru.sbertest.tictactoe.dto.request;

import lombok.Data;

@Data
public class MakeMove {
    private Integer row;
    private Integer column;
}
