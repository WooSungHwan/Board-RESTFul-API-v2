package com.example.board_v2.service;

import com.example.board_v2.entity.Board;
import com.example.board_v2.param.AddBoardParam;
import com.example.board_v2.param.EditBoardParam;
import com.example.board_v2.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public List<Board> getBoardList() throws Exception {
        return boardRepository.findAll();
    }

    public Board getBoard(Long seq) throws Exception {
        return getBoardOrElseThrow(seq);
    }

    @Transactional
    public Board editBoard(EditBoardParam param, Long seq) throws Exception {
        Board board = getBoardOrElseThrow(seq);
        if(board != null) {
            board.setContent(param.getContent());
        }
        return board;
    }

    @Transactional
    public Board addBoard(AddBoardParam param) throws Exception {
        return boardRepository.save(new Board(param));
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
        return boardRepository
                .findById(seq)
                .orElseThrow(() -> new RuntimeException("해당 게시글이 존재하지 않습니다."));
    }
}
