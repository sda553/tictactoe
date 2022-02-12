package ru.sbertest.tictactoe.repository;

import org.springframework.data.repository.CrudRepository;
import ru.sbertest.tictactoe.model.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends CrudRepository<Player,Long> {
    Optional<Player> findBySessionId(UUID sessionId);
}
