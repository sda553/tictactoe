package ru.sbertest.tictactoe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sbertest.tictactoe.dto.request.MakeMove;
import ru.sbertest.tictactoe.dto.request.NewGameRequestDto;
import ru.sbertest.tictactoe.dto.response.StateResponseDto;
import ru.sbertest.tictactoe.exceptions.TicTacToeException;
import ru.sbertest.tictactoe.service.GameService;

import javax.servlet.http.HttpSession;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @GetMapping("/current")
    public Object curGame(HttpSession session) {
        try {
            return service.getCurGame(session);
        } catch (TicTacToeException e) {
            return new ResponseEntity<>(Map.of("detail", e.getGameMessage()), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public StateResponseDto newGame(@RequestBody NewGameRequestDto params , HttpSession session) {
        return service.createNewGame(params,session);
    }

    @PutMapping("/move")
    public Object makeMove(@RequestBody MakeMove params , HttpSession session) {
        try {
            return service.makeMove(params,session);
        } catch (TicTacToeException e) {
            return new ResponseEntity<>(Map.of("detail", e.getGameMessage()), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/undo")
    public Object undoMove(HttpSession session) {
        try {
            return service.undoMove(session);
        } catch (TicTacToeException e) {
            return new ResponseEntity<>(Map.of("detail", e.getGameMessage()), HttpStatus.FORBIDDEN);
        }
    }

}
