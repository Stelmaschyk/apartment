package repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.model.User;
import com.apartment.apartment.repository.user.UserRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import util.TestRepositoryDataProvider;

@SpringBootTest(classes = ApartmentApplication.class)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "databases/user/repository/add-all-user-related-tables.sql"));
        }
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "databases/delete-data-from-all-tables.sql"));
        }
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUser() {
        User user = TestRepositoryDataProvider.createValidUser();
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should check if email exists in the database")
    void shouldCheckIfEmailExists() {
        assertThat(userRepository.existsByEmail("manager@gmail.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should find user by email along with roles")
    void shouldFindUserByEmailWithRoles() {
        Optional<User> userFromDb = userRepository.findByEmail("user@gmail.com");

        assertThat(userFromDb).isPresent();
        assertThat(userFromDb.get().getEmail()).isEqualTo("user@gmail.com");
        assertThat(userFromDb.get().getRoles()).isNotNull();
    }
}
