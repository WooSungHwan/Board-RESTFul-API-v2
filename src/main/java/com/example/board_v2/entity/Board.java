package com.example.board_v2.entity;

import com.example.board_v2.param.AddBoardParam;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.*;

@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long seq;

    private String username;

    @Column(length = 300)
    private String content;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Board(String username, String content) {
        this.username = username;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Board(AddBoardParam param) {
        this.username = param.getUsername();
        this.content = param.getContent();
        this.createdAt = LocalDateTime.now();
    }
}
