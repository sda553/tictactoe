package ru.sbertest.tictactoe.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sbertest.tictactoe.model.State;

public interface StateRepository extends CrudRepository<State,Long> {
}
