package com.example.board_v2.controller;

import com.example.board_v2.entity.Board;
import com.example.board_v2.param.AddBoardParam;
import com.example.board_v2.param.EditBoardParam;
import com.example.board_v2.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/boards")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity addBoard(@RequestBody AddBoardParam param) throws Exception {
        Board board = boardService.addBoard(param);
        URI createdURI = linkTo(BoardController.class).slash(board.getSeq()).toUri();
        return ResponseEntity.created(createdURI).body(board);
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getBoardList() throws Exception {
        return ResponseEntity.ok(boardService.getBoardList());
    }

    @GetMapping(value = "/{seq}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity getBoard(@PathVariable("seq") Long seq) throws Exception {
        Board board = boardService.getBoard(seq);
        if (board == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(board);
        }
    }

    @PutMapping(value = "/{seq}", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity editBoard(@PathVariable("seq") Long seq, @RequestBody EditBoardParam param) throws Exception {
        Board board = boardService.editBoard(param, seq);
        if (board == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok(board);
        }
    }

    @DeleteMapping(value = "{seq}", produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity deleteBoard(@PathVariable("seq") Long seq) throws Exception {
        if (boardService.deleteBoard(seq)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
