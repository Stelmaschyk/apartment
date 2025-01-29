package conroller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.dto.accommodation.AccommodationRequestDto;
import com.apartment.apartment.dto.accommodation.AccommodationResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import util.TestDataProvider;

@SpringBootTest(classes = ApartmentApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccommodationControllerTest {
    protected static MockMvc mockMvc;
    private static final Long TEST_ID = 2L;
    private static final int EXPECTED_LENGTH = 4;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(applicationContext)
            .apply(springSecurity())
            .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                        new ClassPathResource(
                        "databases/accommodations/add-accommodations-to-accommodations-table.sql")

            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/delete-data-from-all-tables.sql")
            );
        }
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Create a new accommodation")
    @Order(1)
    void createAccommodation_ValidRequestDto_Success() throws Exception {
        AccommodationRequestDto requestDto = TestDataProvider.createValidAccommodationRequestDto();

        AccommodationRequestDto expected = new AccommodationRequestDto();
        expected.setSize(requestDto.getSize())
            .setAvailability(requestDto.getAvailability())
            .setDailyRate(requestDto.getDailyRate())
            .setAmenities(requestDto.getAmenities())
                .setAddress(requestDto.getAddress());

        MvcResult result = mockMvc.perform(post("/accommodations")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                AccommodationResponseDto.class);

        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("id")
                .isEqualTo(expected);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Get all accommodation")
    @Order(2)
    void getAllAccommodation_GiveAccommodations_ReturnAllBooksDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/accommodations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto[] actual =
            objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        List<Long> bookIds = Arrays.stream(actual)
                .map(AccommodationResponseDto::getId)
                .toList();

        AssertionsForInterfaceTypes.assertThat(bookIds)
            .hasSize(EXPECTED_LENGTH)
                .containsExactly(1L, 2L, 3L, 4L);
    }

    @WithMockUser(username = "user")
    @Test
    @DisplayName("Retrieve accommodation by id")
    @Order(3)
    void getAccommodationById_GiveAccommodations_ReturnAccommodationResponseDto() throws Exception {
        MvcResult result = mockMvc.perform(get("/accommodations/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                AccommodationResponseDto.class);

        assertThat(actual.getId())
            .isNotNull()
                .isEqualTo(TEST_ID);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Order(4)
    void updateAccommodation_ValidRequestDto_Success() throws Exception {
        AccommodationRequestDto updatedRequestDto =
                TestDataProvider.updatedValidAccommodationRequestDto(TEST_ID);
        MvcResult result = mockMvc.perform(put("/accommodations/{id}", TEST_ID)
                .content(objectMapper.writeValueAsString(updatedRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(),
                AccommodationResponseDto.class);

        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .ignoringFields("id")
                .isEqualTo(updatedRequestDto);
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("delete accommodation by id")
    @Order(5)
    void deleteById_WithValidBookId_Success() throws Exception {
        mockMvc.perform(delete("/accommodations/{id}", TEST_ID)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
                .andReturn();
    }
}
