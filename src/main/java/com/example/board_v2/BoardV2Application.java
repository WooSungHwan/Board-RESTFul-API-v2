package com.example.board_v2;

import com.example.board_v2.entity.Board;
import com.example.board_v2.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
public class BoardV2Application {

	@Autowired
	private BoardRepository boardRepository;

	public static void main(String[] args) {
		SpringApplication.run(BoardV2Application.class, args);
	}

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			for(int i = 1; i <= 10; i++) {
				boardRepository.save(new Board("유저" + i, "유저" + i + "의 게시판 내용"));
			}
		};
	}
	
}
