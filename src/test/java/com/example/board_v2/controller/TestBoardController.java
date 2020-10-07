package com.example.board_v2.controller;

import com.example.board_v2.entity.Board;
import com.example.board_v2.param.AddBoardParam;
import com.example.board_v2.param.EditBoardParam;
import com.example.board_v2.repository.BoardRepository;
import com.example.board_v2.result.BoardResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@SpringBootTest
public class TestBoardController {
    private final String BASE_URL = "/boards";

    private MockMvc mockMvc;
    private RestDocumentationResultHandler document;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BoardRepository boardRepository;
    @Autowired
    WebApplicationContext ctx;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).apply(documentationConfiguration(restDocumentation))
                .addFilter(new CharacterEncodingFilter("UTF-8", true)) // 2.2 버전 이후 mock 객체에서 한글 인코딩 처리해야함. -> 필터추가
                .alwaysDo(print())
                .build();
        document = document("{class-name}/{method-name}", preprocessResponse(prettyPrint()));
    }

    @Order(1)
    @DisplayName("게시글 등록(OK)")
    @Test
    public void addBoard() throws Exception {
        AddBoardParam param = AddBoardParam.builder().content("게시글을 등록합니다.").username("유저1").build();
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                                    .accept(MediaTypes.HAL_JSON_VALUE)
                                    .contentType(MediaTypes.HAL_JSON_VALUE)
                                    .content(objectMapper.writeValueAsString(param)))
                                    .andDo(print())
                                    .andExpect(status().isCreated())
                                    .andDo(document.document(
                                            requestFields(
                                                    fieldWithPath("username").description("유저 아이디"),
                                                    fieldWithPath("content").description("글 내용")
                                            ),
                                            responseFields(
                                                    fieldWithPath("seq").type(JsonFieldType.NUMBER).description("글 번호"),
                                                    fieldWithPath("username").type(JsonFieldType.STRING).description("유저 아이디"),
                                                    fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("글 등록 일자"),
                                                    fieldWithPath("_links").type(JsonFieldType.OBJECT).description("API 링크 정보"),
                                                    fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("현재 API URL"),
                                                    fieldWithPath("_links.get.href").type(JsonFieldType.STRING).description("해당 글 내용 상세 API URL"),
                                                    fieldWithPath("_links.delete.href").type(JsonFieldType.STRING).description("해당 글 삭제 API URL"),
                                                    fieldWithPath("_links.edit.href").type(JsonFieldType.STRING).description("해당 글 수정 API URL")
                                            )
                                    ))
                                    .andReturn();

        BoardResult boardResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BoardResult.class);
        Assertions.assertEquals(boardResult.getContent(), param.getContent());
        Assertions.assertEquals(boardResult.getUsername(), param.getUsername());

        Board savedBoard = boardRepository.findById(boardResult.getSeq()).orElse(null);
        Assertions.assertEquals(savedBoard.getContent(), param.getContent());
        Assertions.assertEquals(savedBoard.getUsername(), param.getUsername());
    }

    @Order(2)
    @DisplayName("게시글 리스트 조회 - 페이징(OK)")
    @Test
    public void getBoardList() throws Exception {
        MultiValueMap<String, String> query = new LinkedMultiValueMap<>();
        query.add("size", "5");
        query.add("page", "1");

        mockMvc.perform(get(BASE_URL)
               .accept(MediaTypes.HAL_JSON_VALUE)
               .params(query))
               .andDo(print())
               .andExpect(status().isOk())
               .andDo(document.document(
                       requestParameters(
                               parameterWithName("size").description("사이즈"),
                               parameterWithName("page").description("페이지정보 (0 부터 시작.)")
                       ),
                       responseFields(
                               fieldWithPath("_embedded").type(JsonFieldType.OBJECT).description("결과 내용"),
                               fieldWithPath("_embedded.boardResultList[]").type(JsonFieldType.ARRAY).description("글 내용 결"),
                               fieldWithPath("_embedded.boardResultList[].seq").type(JsonFieldType.NUMBER).description("글 번호"),
                               fieldWithPath("_embedded.boardResultList[].username").type(JsonFieldType.STRING).description("유저 아이디"),
                               fieldWithPath("_embedded.boardResultList[].content").type(JsonFieldType.STRING).description("글 내용"),
                               fieldWithPath("_embedded.boardResultList[].createdAt").type(JsonFieldType.STRING).description("글 등록 일자"),
                               fieldWithPath("_embedded.boardResultList[]._links").type(JsonFieldType.OBJECT).description("API 링크 정보"),
                               fieldWithPath("_embedded.boardResultList[]._links.get.href").type(JsonFieldType.STRING).description("해당 글 내용 상세 API URL"),
                               fieldWithPath("_embedded.boardResultList[]._links.delete.href").type(JsonFieldType.STRING).description("해당 글 삭제 API URL"),
                               fieldWithPath("_embedded.boardResultList[]._links.edit.href").type(JsonFieldType.STRING).description("해당 글 수정 API URL"),
                               fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("해당 글 수정 API URL")
                       )
               ))
        ;
    }

    @Order(3)
    @DisplayName("게시글 1건 조회(OK)")
    @Test
    public void getBoard() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{seq}", 1L)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                                parameterWithName("seq").description("글 번호")
                        ),
                        responseFields(
                                fieldWithPath("seq").type(JsonFieldType.NUMBER).description("글 번호"),
                                fieldWithPath("username").type(JsonFieldType.STRING).description("유저 아이디"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("글 등록 일자"),
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("API 링크 정보"),
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("현재 API URL"),
                                fieldWithPath("_links.delete.href").type(JsonFieldType.STRING).description("해당 글 삭제 API URL"),
                                fieldWithPath("_links.edit.href").type(JsonFieldType.STRING).description("해당 글 수정 API URL")
                        )
                ));
    }

    @Order(4)
    @DisplayName("게시글 1건 조회(NOF FOUND)")
    @Test
    public void getBoard_NotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{seq}", -1L)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Order(5)
    @DisplayName("게시글 수정(OK)")
    @Test
    public void editBoard() throws Exception {
        EditBoardParam param = EditBoardParam.builder().content("수정하는 게시글입니다.").build();

        MvcResult mvcResult = mockMvc.perform(put(BASE_URL + "/{seq}", 1L)
                                    .contentType(MediaTypes.HAL_JSON_VALUE)
                                    .accept(MediaTypes.HAL_JSON_VALUE)
                                    .content(objectMapper.writeValueAsString(param)))
                                    .andDo(print())
                                    .andExpect(status().isOk())
                                    .andDo(document.document(
                                            pathParameters(
                                                    parameterWithName("seq").description("글 번호")
                                            ),
                                            responseFields(
                                                    fieldWithPath("seq").type(JsonFieldType.NUMBER).description("글 번호"),
                                                    fieldWithPath("username").type(JsonFieldType.STRING).description("유저 아이디"),
                                                    fieldWithPath("content").type(JsonFieldType.STRING).description("글 내용"),
                                                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("글 등록 일자"),
                                                    fieldWithPath("_links").type(JsonFieldType.OBJECT).description("API 링크 정보"),
                                                    fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("현재 API URL"),
                                                    fieldWithPath("_links.get.href").type(JsonFieldType.STRING).description("해당 글 상세 API URL"),
                                                    fieldWithPath("_links.list.href").type(JsonFieldType.STRING).description("글 목록 API URL")
                                            )
                                    ))
                                    .andReturn();
        BoardResult boardResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BoardResult.class);
        Assertions.assertEquals(param.getContent(), boardResult.getContent());
        Assertions.assertEquals(param.getContent(), boardRepository.findById(1L).orElse(null).getContent());
    }

    @Order(6)
    @DisplayName("게시글 수정(Not Found)")
    @Test
    public void editBoard_NotFound() throws Exception {
        EditBoardParam param = EditBoardParam.builder().content("수정하는 게시글입니다.").build();

        mockMvc.perform(put(BASE_URL + "/{seq}", -2L)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Order(7)
    @DisplayName("게시글 수정(content 길이 300 초과)")
    @Test
    public void editBoard_ContentLengthOver() throws Exception {
        StringBuilder sb = new StringBuilder();
        for(int i =0; i<301; i++) {
            sb.append("a");
        }
        EditBoardParam param = EditBoardParam.builder().content(sb.toString()).build();
        mockMvc.perform(put(BASE_URL + "/{seq}", 1L)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Order(7)
    @DisplayName("게시글 수정(content 길이 2 미만)")
    @Test
    public void editBoard_ContentLengthUnder() throws Exception {
        EditBoardParam param = EditBoardParam.builder().content("아").build();
        mockMvc.perform(put(BASE_URL + "/{seq}", 1L)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Order(7)
    @DisplayName("게시글 수정(content null)")
    @Test
    public void editBoard_ContentNull() throws Exception {
        EditBoardParam param = EditBoardParam.builder().content(null).build();
        mockMvc.perform(put(BASE_URL + "/{seq}", 1L)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Order(10)
    @DisplayName("게시글 삭제(OK)")
    @Test
    public void deleteBoard() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{seq}", 1L)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document.document(
                        pathParameters(
                            parameterWithName("seq").description("글 번호")
                        ),
                        responseFields(
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("API 링크 정보"),
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("현재 API URL"),
                                fieldWithPath("deletedSeq").type(JsonFieldType.NUMBER).description("삭제된 시퀀스 번호")
                        )
                ))
        ;

        Assertions.assertNull(boardRepository.findById(1L).orElse(null));
    }

    @Order(11)
    @DisplayName("게시글 삭제(Not Found)")
    @Test
    public void deleteBoard_NotFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{seq}", -1L)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}