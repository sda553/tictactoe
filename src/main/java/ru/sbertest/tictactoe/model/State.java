package ru.sbertest.tictactoe.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name="states")
@Data
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="prev_state_id")
    private State prevState;

    @OneToOne
    @JoinColumn(name="next_state_id")
    private State nextState;

    @Column(name="state")
    private Integer state;
}
