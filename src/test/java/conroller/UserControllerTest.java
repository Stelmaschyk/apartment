package conroller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.dto.user.UpdateRoleRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import util.TestControllerDataProvider;

@SpringBootTest(classes = ApartmentApplication.class)
public class UserControllerTest {
    protected static MockMvc mockMvc;
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_EMAIL = "user@gmail.com";
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
                    "databases/roles/controller/add-roles.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/user/controller/add-user-to-tables-users.sql")
            );
            ScriptUtils.executeSqlScript(
                    connection,
                new ClassPathResource(
                    "databases/user/controller/add-roles-to users-to-users-roles-table.sql")
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

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    void getInformationWithLoginedUser_withAuthenticatedUser_getUserResponseDto() throws Exception {
        UserResponseDto expected = TestControllerDataProvider.createValidUserResponseDto();

        MvcResult result = mockMvc.perform(get("/users/me", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);

        assertThat(actual.getId())
            .isNotNull()
                .isEqualTo(expected.getId());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void updateRoleById_withAuthenticatedAdminUser_updateRoleResponseDto() throws Exception {
        UpdateRoleRequestDto requestDto =
                TestControllerDataProvider.createValidUpdateRoleRequestDto();
        UserRoleResponseDto expected = TestControllerDataProvider.createValidUserRolesResponseDto();

        MvcResult result = mockMvc.perform(put("/users/{id}/role", TEST_USER_ID)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserRoleResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserRoleResponseDto.class);

        assertThat(actual.roleIds())
            .isNotNull()
                .isEqualTo(expected.roleIds());
    }

    @WithUserDetails(value = TEST_USER_EMAIL)
    @Test
    void updateUserInfo_withAuthenticatedUser_updateUserInfoResponseDto() throws Exception {
        UserUpdateRequestDto requestDto =
                TestControllerDataProvider.createValidUserUpdateRequestDto();

        MvcResult result = mockMvc.perform(patch("/users/me", TEST_USER_ID)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), UserResponseDto.class);

        assertThat(actual)
            .isNotNull()
            .usingRecursiveComparison()
            .comparingOnlyFields("firstName", "lastName")
                .isEqualTo(requestDto);
    }
}
