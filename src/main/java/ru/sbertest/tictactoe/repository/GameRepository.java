package ru.sbertest.tictactoe.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.sbertest.tictactoe.model.Game;

import java.util.List;

public interface GameRepository  extends PagingAndSortingRepository<Game, Long> {
    @Override
    Page<Game> findAll(Pageable pageable);

    @Modifying
    @Query("update Game gm set gm.result=ru.sbertest.tictactoe.common.ResultState.FINISHED where gm.playerId = :playerId")
    void closeAllGames(@NonNull @Param("playerId") Long playerId);

    @Query("select gm from Game gm where gm.result=ru.sbertest.tictactoe.common.ResultState.IN_PROCESS and gm.playerId = :playerId")
    List<Game> findCurrentGame(@NonNull @Param("playerId") Long playerId);
}
