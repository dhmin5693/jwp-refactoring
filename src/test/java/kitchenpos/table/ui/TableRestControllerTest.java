package kitchenpos.table.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import kitchenpos.config.MockMvcTestConfig;
import kitchenpos.table.domain.OrderTable;
import kitchenpos.table.dto.CreateOrderTableDto;
import kitchenpos.table.dto.OrderTableDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@MockMvcTestConfig
class TableRestControllerTest {

    private static final String BASE_URL = "/api/tables";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("주문 테이블 생성 요청 성공")
    @Test
    void createOrderTableRequestSuccess() throws Exception {
        CreateOrderTableDto createOrderTableDto = new CreateOrderTableDto(3, false);
        createOrderTableRequest(createOrderTableDto);
    }

    @DisplayName("주문 테이블 목록 요청 성공")
    @Test
    void getOrderTablesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_URL))
                                  .andDo(print())
                                  .andExpect(status().isOk())
                                  .andReturn();

        String content = result.getResponse().getContentAsString();
        List<OrderTableDto> list = Arrays.asList(objectMapper.readValue(content, OrderTableDto[].class));
        assertThat(list).hasSizeGreaterThanOrEqualTo(8); // default data size
    }

    @DisplayName("주문 테이블을 빈 상태로 변경 요청 성공")
    @Test
    void updateOrderTableEmptyStatusSuccess() throws Exception {

        MvcResult result = mockMvc.perform(put(BASE_URL + "/1/empty")
                                               .content(objectMapper.writeValueAsString(true))
                                               .contentType(MediaType.APPLICATION_JSON))
                                  .andDo(print())
                                  .andExpect(status().isOk())
                                  .andReturn();

        String content = result.getResponse().getContentAsString();
        OrderTable response = objectMapper.readValue(content, OrderTable.class);
        assertTrue(response.isEmpty());
    }

    @DisplayName("주문 테이블을 빈 상태로 변경 요청 실패 - 주문 테이블 그룹에 속한 상태")
    @Test
    void updateOrderTableEmptyStatusFail01() {
        // V2__Insert_default_data.sql에서 9번 데이터 삽입
        putEmptyFail(9);
    }

    @DisplayName("주문 테이블을 빈 상태로 변경 요청 실패 - 주문 상태가 COOKING 또는 MEAL")
    @ValueSource(ints = {10, 11})
    @ParameterizedTest
    void updateOrderTableEmptyStatusFail02(int id) {
        // V2__Insert_default_data.sql에서 10, 11번 데이터 삽입
        putEmptyFail(id);
    }

    @DisplayName("주문 테이블의 손님 수 변경 요청 성공")
    @Test
    void updateOrderTableNumberOfGuestsSuccess() throws Exception {

        Integer request = 3;

        MvcResult result = mockMvc.perform(put(BASE_URL + "/9/number-of-guests")
                                               .content(objectMapper.writeValueAsString(request))
                                               .contentType(MediaType.APPLICATION_JSON))
                                  .andDo(print())
                                  .andExpect(status().isOk())
                                  .andReturn();

        String content = result.getResponse().getContentAsString();
        OrderTable response = objectMapper.readValue(content, OrderTable.class);
        assertEquals(request, response.getNumberOfGuests());
    }

    @DisplayName("주문 테이블의 손님 수 변경 요청 실패 - 손님 수가 음수")
    @ValueSource(ints = {-1, -5})
    @ParameterizedTest
    void updateOrderTableNumberOfGuestsFail01(int numberOfGuests) {
        putNumberOfGuestsFail(1, numberOfGuests);
    }

    @DisplayName("주문 테이블의 손님 수 변경 요청 실패 - empty인 주문 테이블")
    @Test
    void updateOrderTableNumberOfGuestsFail02() {
        putNumberOfGuestsFail(4, 3);
    }

    private void createOrderTableRequest(CreateOrderTableDto createOrderTableDto) throws Exception {
        mockMvc.perform(post(BASE_URL).content(objectMapper.writeValueAsString(createOrderTableDto))
                                      .contentType(MediaType.APPLICATION_JSON))
               .andDo(print())
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }

    private void putEmptyFail(int id) {
        try {
            mockMvc.perform(put(BASE_URL + "/" + id + "/empty")
                                .content(objectMapper.writeValueAsString(true))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andReturn();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    private void putNumberOfGuestsFail(int id, int numberOfGuests) {
        try {
            mockMvc.perform(put(BASE_URL + "/" + id + "/number-of-guests")
                                .content(objectMapper.writeValueAsString(numberOfGuests))
                                .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isBadRequest())
                   .andReturn();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }
}
