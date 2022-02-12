package ru.sbertest.tictactoe.service;

import org.springframework.stereotype.Service;
import ru.sbertest.tictactoe.model.State;
import ru.sbertest.tictactoe.repository.StateRepository;

@Service
public class StateService {

    private final StateRepository repository;

    public StateService(StateRepository repository) {
        this.repository = repository;
    }

    public State save(State state) {
        return repository.save(state);
    }
}
