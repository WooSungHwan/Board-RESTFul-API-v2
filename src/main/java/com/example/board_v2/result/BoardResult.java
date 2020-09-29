package com.example.board_v2.result;

import com.example.board_v2.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class BoardResult {
    private Long seq;
    private String username;
    private String content;
    private String createdAt;

    public BoardResult(Board board) {
        this.seq = board.getSeq();
        this.username = board.getUsername();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }
}
