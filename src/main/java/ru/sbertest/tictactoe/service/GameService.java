package ru.sbertest.tictactoe.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.sbertest.tictactoe.common.CellType;
import ru.sbertest.tictactoe.dto.request.MakeMove;
import ru.sbertest.tictactoe.dto.request.NewGameRequestDto;
import ru.sbertest.tictactoe.dto.response.StateResponseDto;
import ru.sbertest.tictactoe.exceptions.TicTacToeException;
import ru.sbertest.tictactoe.model.Game;
import ru.sbertest.tictactoe.model.Player;
import ru.sbertest.tictactoe.model.State;
import ru.sbertest.tictactoe.repository.GameRepository;
import ru.sbertest.tictactoe.repository.PlayerRepository;
import ru.sbertest.tictactoe.repository.StateRepository;
import ru.sbertest.tictactoe.common.PlayerType;
import ru.sbertest.tictactoe.common.Utils;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ru.sbertest.tictactoe.common.ResultState.FINISHED;
import static ru.sbertest.tictactoe.common.ResultState.IN_PROCESS;

@Service
@AllArgsConstructor
public class GameService {
    private final GameRepository repository;
    private final StateRepository stateRepository;
    private final PlayerRepository playerRepository;

    public Page<Game> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }



    @Transactional
    public StateResponseDto createNewGame(NewGameRequestDto params, HttpSession session) {
        Player player = getPlayer(session);
        //close other games
        repository.closeAllGames(player.getId());
        //create new state
        State newState = new State();
        newState.setPrevState(null);
        newState.setNextState(null);
        CellType[][] state;
        if (params.getFirstMove().equals(PlayerType.MACHINE))
            state = new CellType[][]{{CellType.EMPTY, CellType.EMPTY, CellType.EMPTY}, {CellType.EMPTY, CellType.TIC, CellType.EMPTY}, {CellType.EMPTY, CellType.EMPTY, CellType.EMPTY}};
        else
            state =  new CellType[][]{{CellType.EMPTY, CellType.EMPTY, CellType.EMPTY}, {CellType.EMPTY, CellType.EMPTY, CellType.EMPTY}, {CellType.EMPTY, CellType.EMPTY, CellType.EMPTY}};
        newState.setState(Utils.stateToInt(state));
        newState = stateRepository.save(newState);
        //create new game
        Game newGame = new Game();
        newGame.setState(newState);
        newGame.setPlayer(player);
        newGame.setResult(IN_PROCESS);
        repository.save(newGame);
        player.setLastGame(newGame);
        playerRepository.save(player);
        return new StateResponseDto(newGame);
    }

    private Player getPlayer(HttpSession session) {
        //get current player
        Object playerUid = session.getAttribute("player");
        if (Objects.isNull(playerUid)) {
            playerUid = UUID.randomUUID();
            session.setAttribute("player",playerUid);
        }
        Player player = playerRepository.findBySessionId((UUID) playerUid).orElse(new Player());
        if (Objects.isNull(player.getId())) {
            //create new player
            player.setSessionId((UUID) playerUid);
            player = playerRepository.save(player);
        }
        return player;
    }

    private void validateParam(MakeMove params) throws TicTacToeException {
        if (params.getRow()==null || params.getRow()<0 || params.getRow()>2)
            throw new TicTacToeException("Illegal move!");
        if (params.getColumn()==null || params.getColumn()<0 || params.getColumn()>2)
            throw new TicTacToeException("Illegal move!");
    }



    @Transactional
    public StateResponseDto makeMove(MakeMove params, HttpSession session) throws TicTacToeException {
        validateParam(params);
        Player player = getPlayer(session);
        List<Game> gamesOfPlayer = repository.findCurrentGame(player.getId());
        if (gamesOfPlayer.size()==0) {
            throw new TicTacToeException("No active games started!");
        }
        Game activeGame = gamesOfPlayer.get(0);
        State curPersState = activeGame.getState();
        CellType[][] curState = Utils.intToState(curPersState.getState());
        if (!curState[params.getRow()][params.getColumn()].equals(CellType.EMPTY)) {
            throw new TicTacToeException("The cell is not empty!");
        }
        CellType[][] newState = new CellType[3][3];
        int emptyCount = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                newState[row][col] = curState[row][col];
                if (curState[row][col].equals(CellType.EMPTY)) {
                    emptyCount++;
                }
            }
        }
        newState[params.getRow()][params.getColumn()]=(emptyCount % 2 == 0)? CellType.TAC: CellType.TIC;
        if (winGame(newState)) {
            activeGame.setResult(FINISHED);
            activeGame.setWinner(PlayerType.HUMAN);
        } else if (emptyCount==1) {
            //No free cells to make a move
            activeGame.setResult(FINISHED);
            activeGame.setWinner(null);
        }
        else {
            CellType machineMove = (emptyCount % 2 == 0) ? CellType.TIC : CellType.TAC;
            emptyCount--;
            //make random move
            int machineRandomMove;
            if (emptyCount==1) {
                activeGame.setResult(FINISHED);
                machineRandomMove = 0;
            } else {
                machineRandomMove = UUID.randomUUID().hashCode();
                if (machineRandomMove < 0) machineRandomMove = -machineRandomMove;
                machineRandomMove = (machineRandomMove % (emptyCount - 1));
            }
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    if (newState[row][col].equals(CellType.EMPTY)) {
                        emptyCount--;
                        if (emptyCount == machineRandomMove) {
                            newState[row][col] = machineMove;
                            break;
                        }
                    }
                }
            }
            if (winGame(newState)) {
                activeGame.setResult(FINISHED);
                activeGame.setWinner(PlayerType.MACHINE);
            }
        }
        State newPersState = new State();
        newPersState.setPrevState(curPersState);
        newPersState.setState(Utils.stateToInt(newState));
        newPersState = stateRepository.save(newPersState);
        curPersState.setNextState(newPersState);
        stateRepository.save(curPersState);
        activeGame.setState(newPersState);
        repository.save(activeGame);
        return new StateResponseDto(activeGame);
    }

    private boolean winGame(CellType[][] state) {
        boolean result = false;
        int[] lines = {1,1,1,1,1,1,1,1};
        for (int i = 0; i<3; i++)
            for (int j = 0; j<3; j++) {
                lines[i] = lines[i]*state[i][j].getValue();
                lines[3+j] =lines[3+j]*state[i][j].getValue();
                lines[6] = (i==j)? lines[6]*state[i][j].getValue():lines[6];
                lines[7] = (i==2-j)?lines[7]*state[i][j].getValue():lines[7];
            }
        for (int i=0;i<8;i++) {
            if (lines[i]==1 || lines[i]==8) {
                result = true;
                break;
            }
        }
        return result;
    }

    public Object undoMove(HttpSession session) throws TicTacToeException {
        Player player = getPlayer(session);
        Game activeGame =  player.getLastGame();
        if (activeGame==null)
            throw new TicTacToeException("No moves to undo! No any game started!");
        if (activeGame.getState().getPrevState()==null)
            throw new TicTacToeException("No moves to undo! It was the moment game started!");
        activeGame.setWinner(null);
        activeGame.setResult(IN_PROCESS);
        activeGame.setState(activeGame.getState().getPrevState());
        repository.save(activeGame);
        return new StateResponseDto(activeGame);
    }

    public Object getCurGame(HttpSession session) throws TicTacToeException {
        Player player = getPlayer(session);
        Game activeGame =  player.getLastGame();
        if (activeGame==null)
            throw new TicTacToeException("No any game been played!");
        return new StateResponseDto(activeGame);
    }
}
