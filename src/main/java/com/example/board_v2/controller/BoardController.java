package com.example.board_v2.controller;

import com.example.board_v2.entity.Board;
import com.example.board_v2.param.AddBoardParam;
import com.example.board_v2.param.EditBoardParam;
import com.example.board_v2.result.BoardResult;
import com.example.board_v2.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping("/boards")
@RequiredArgsConstructor
@RestController
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 등록
     * @param param
     * @return
     * @throws Exception
     */
    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity addBoard(@RequestBody @Validated AddBoardParam param) throws Exception {
        BoardResult boardResult = boardService.addBoard(param);
        URI createdURI = getLinkAddress().slash(boardResult.getSeq()).toUri();
        EntityModel<BoardResult> entityModel = EntityModel.of(boardResult,
                getLinkAddress().slash(boardResult.getSeq()).withSelfRel(),
                getLinkAddress().slash(boardResult.getSeq()).withRel("get"),
                getLinkAddress().slash(boardResult.getSeq()).withRel("delete"),
                getLinkAddress().slash(boardResult.getSeq()).withRel("edit"));

        return ResponseEntity.created(createdURI).body(entityModel);
    }

    /**
     * 게시글 리스트
     * @return
     * @throws Exception
     */
@GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
public ResponseEntity getBoardList() throws Exception {
    List<BoardResult> boardList = boardService.getBoardList();
    // 각 요소를 EntityModel로 변환.
    List<EntityModel> collect = boardList.stream()
                                    .map(board -> EntityModel.of(board,
                                            getLinkAddress().slash(board.getSeq()).withRel("get"),
                                            getLinkAddress().slash(board.getSeq()).withRel("delete")))
                                    .collect(Collectors.toList());

    // 리스트를 CollectionModel로 변환. -> response body에 담는다.
    CollectionModel entityModel = CollectionModel.of(collect, getLinkAddress().withSelfRel());
    return ResponseEntity.ok(entityModel);
}

    /**
     * 게시글 상세
     * @param seq
     * @return
     * @throws Exception
     */
@GetMapping(value = "/{seq}", produces = MediaTypes.HAL_JSON_VALUE)
public ResponseEntity getBoard(@PathVariable("seq") Long seq) throws Exception {
    BoardResult boardResult = boardService.getBoard(seq);
    if (boardResult == null) {
        return ResponseEntity.notFound().build();
    } else {
        EntityModel entityModel = EntityModel.of(boardResult,
                getLinkAddress().slash(boardResult.getSeq()).withSelfRel(),
                getLinkAddress().slash(boardResult.getSeq()).withRel("delete"),
                getLinkAddress().slash(boardResult.getSeq()).withRel("edit"));
        return ResponseEntity.ok(entityModel);
    }
}

    /**
     * 게시글 수정
     * @param seq
     * @param param
     * @return
     * @throws Exception
     */
@PutMapping(value = "/{seq}", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaTypes.HAL_JSON_VALUE)
public ResponseEntity editBoard(@PathVariable("seq") Long seq, @RequestBody @Validated EditBoardParam param) throws Exception {
    BoardResult boardResult = boardService.editBoard(param, seq);
    if (boardResult == null) {
        return ResponseEntity.notFound().build();
    }

    EntityModel<BoardResult> entityModel = EntityModel.of(boardResult,
            getLinkAddress().slash(boardResult.getSeq()).withSelfRel(),
            getLinkAddress().slash(boardResult.getSeq()).withRel("get"),
            getLinkAddress().withRel("list"));
    return ResponseEntity.ok(entityModel);
}

    /**
     * 게시글 삭제
     * @param seq
     * @return
     * @throws Exception
     */
@DeleteMapping(value = "{seq}", produces = MediaTypes.HAL_JSON_VALUE)
public ResponseEntity deleteBoard(@PathVariable("seq") Long seq) throws Exception {
    if (boardService.deleteBoard(seq)) {
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("deletedSeq", seq);
        EntityModel entityModel = EntityModel.of(resultMap, getLinkAddress().slash(seq).withSelfRel());
        return ResponseEntity.ok(entityModel);
    }
    return ResponseEntity.notFound().build();
}

    private WebMvcLinkBuilder getLinkAddress() {
        return linkTo(BoardController.class);
    }

}
