package ru.sbertest.tictactoe.model;

import lombok.Data;
import ru.sbertest.tictactoe.common.PlayerType;
import ru.sbertest.tictactoe.common.ResultState;

import javax.persistence.*;

@Entity
@Table(name="games")
@Data
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="state_id")
    private State state;

    @Column(name="winner")
    @Enumerated(EnumType.STRING)
    private PlayerType winner;

    @ManyToOne
    @JoinColumn(name="player_id")
    private Player player;

    @Column(name="player_id", insertable = false, updatable = false)
    private Long playerId;

    @Column(name="result")
    @Enumerated(EnumType.STRING)
    private ResultState result;

}
