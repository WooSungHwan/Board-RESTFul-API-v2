package com.example.board_v2.param;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AddBoardParam {
    private String username;
    private String content;
}
