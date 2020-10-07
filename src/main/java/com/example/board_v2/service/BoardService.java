package com.example.board_v2.service;

import com.example.board_v2.entity.Board;
import com.example.board_v2.param.AddBoardParam;
import com.example.board_v2.param.EditBoardParam;
import com.example.board_v2.repository.BoardRepository;
import com.example.board_v2.result.BoardResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<BoardResult> getBoardList(Pageable pageable) throws Exception {
        return boardRepository.findAll(pageable)
                .get()
                .map(BoardResult::new)
                .collect(Collectors.toList());
    }

    public BoardResult getBoard(Long seq) throws Exception {
        return new BoardResult(getBoardOrElseThrow(seq));
    }

    @Transactional
    public BoardResult editBoard(EditBoardParam param, Long seq) throws Exception {
        Board board = getBoardOrElseThrow(seq);
        if(board != null) {
            board.setContent(param.getContent());
        }
        return new BoardResult(board);
    }

    @Transactional
    public BoardResult addBoard(AddBoardParam param) throws Exception {
        Board entity = new Board(param);
        return new BoardResult(boardRepository.save(entity));
    }

    @Transactional
    public boolean deleteBoard(Long seq) throws Exception {
        Board board = getBoardOrElseThrow(seq);
        if (board == null) {
            return false;
        }
        boardRepository.delete(board);
        return true;
    }

    private Board getBoardOrElseThrow(Long seq) {
        //RuntimeException -> EntityNotFoundException으로 변경
        return boardRepository.findById(seq)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글이 존재하지 않습니다."));
    }
}
