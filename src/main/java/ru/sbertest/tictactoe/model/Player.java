package ru.sbertest.tictactoe.model;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="players")
@Data
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="session_id")
    UUID sessionId;

    @ManyToOne
    @JoinColumn(name="game_id")
    Game lastGame;
}
