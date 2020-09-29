package com.example.board_v2.param;

import lombok.*;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AddBoardParam {
    private String username;
    @Length(min = 2, max = 300, message = "최소 {min}자 이상 최대 {max}자 이하로 입력해주시기 바랍니다.")
    private String content;
}
