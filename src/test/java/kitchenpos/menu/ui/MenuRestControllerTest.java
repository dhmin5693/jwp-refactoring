package kitchenpos.menu.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import kitchenpos.config.MockMvcTestConfig;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.dto.CreateMenuDto;
import kitchenpos.menu.dto.CreateMenuProductDto;
import kitchenpos.menu.dto.MenuDto;
import kitchenpos.menu.dto.MenuProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@MockMvcTestConfig
@SuppressWarnings("NonAsciiCharacters")
public class MenuRestControllerTest {

    private static final String BASE_URL = "/api/menus";
    private static final long NOT_SAVED_ID = Long.MAX_VALUE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("메뉴 생성 요청 성공")
    @Test
    void createMenuRequestSuccess() throws Exception {
        CreateMenuDto menuDto = createMenuDto(30000L);

        String content = objectMapper.writeValueAsString(menuDto);

        mockMvc.perform(post(BASE_URL).content(content)
                                      .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }

    @DisplayName("메뉴 생성 요청 실패 - 메뉴 가격이 음수이거나 설정되지 않음")
    @NullSource
    @ValueSource(longs = { -1000, -10000 })
    @ParameterizedTest
    void createMenuRequestFail01(Long price) {
        CreateMenuDto menuDto = createMenuDto(price);
        createMenuFail(menuDto);
    }

    @DisplayName("메뉴 생성 요청 실패 - 등록되지 않은 메뉴 그룹의 ID를 사용")
    @Test
    void createMenuRequestFail02() {
        CreateMenuDto menuDto = createMenuDto(30000L, NOT_SAVED_ID);
        createMenuFail(menuDto);
    }

    @DisplayName("메뉴 생성 요청 실패 - 메뉴 가격은 메뉴 상품 가격의 합보다 같거나 작아야 함")
    @ValueSource(longs = { 33000, 35000 })
    @ParameterizedTest
    void createMenuRequestFail03(Long price) {

        // 테스트 케이스에서 사용된 메뉴 상품 가격
        // 후라이드: 16000원 * 1개
        // 양념: 16000원 * 1개
        // 총 32000원

        CreateMenuDto menuDto = createMenuDto(price);
        createMenuFail(menuDto);
    }

    @DisplayName("메뉴 목록 조회 요청")
    @Test
    void findMenus() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL))
                                  .andDo(print())
                                  .andExpect(status().isOk())
                                  .andReturn();

        String response = result.getResponse().getContentAsString();
        List<MenuDto> list = Arrays.asList(objectMapper.readValue(response, MenuDto[].class));
        assertThat(list).hasSizeGreaterThanOrEqualTo(6); // default data size is 6
    }

    private void createMenuFail(CreateMenuDto menuDto) {
        try {
            String content = objectMapper.writeValueAsString(menuDto);
            mockMvc.perform(post(BASE_URL).content(content)
                                          .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail();
        }
    }

    public CreateMenuDto createMenuDto(Long price) {
        return createMenuDto(price, 1L);
    }

    public CreateMenuDto createMenuDto(Long price, Long menuGroupId) {
        // Product id 1: 후라이드, 2: 양념, 둘 다 16000원
        CreateMenuProductDto 후라이드 = new CreateMenuProductDto(1L, 1L);
        CreateMenuProductDto 양념 = new CreateMenuProductDto(2L, 1L);

        List<CreateMenuProductDto> menuProductDtos = Arrays.asList(후라이드, 양념);

        return new CreateMenuDto("후라이드양념두마리세트", price, menuGroupId, menuProductDtos);
    }
}
