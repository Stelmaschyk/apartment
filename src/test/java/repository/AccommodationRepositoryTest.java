package repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.apartment.apartment.ApartmentApplication;
import com.apartment.apartment.model.Accommodation;
import com.apartment.apartment.repository.accommodation.AccommodationRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
public class AccommodationRepositoryTest {
    private static final Long TEST_VALID_ACCOMMODATION_ID = 1L;
    private static final int ACCOMMODATION_LIST_SIZE = 4;
    private static final Long INCORRECT_ACCOMMODATION_ID = 80L;
    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "databases/accommodations/repository/"
                        + "add-all-accommodation-related-tables.sql"));
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
    @DisplayName("Test findAll() should return a list of accommodations")
    public void testFindAll() {
        List<Accommodation> accommodations = accommodationRepository.findAll();

        assertThat(accommodations).isNotEmpty()
            .hasSizeGreaterThan(0)
            .hasSize(ACCOMMODATION_LIST_SIZE);
    }

    @Test
    @DisplayName("Test save() should save and return a new accommodation")
    public void save_withValidAccommodation_shouldSave() {
        Accommodation accommodation = TestRepositoryDataProvider.createValidAccommodation();
        Accommodation savedAccommodation = accommodationRepository.save(accommodation);

        assertThat(savedAccommodation.getId()).isNotNull();
        assertThat(savedAccommodation.getAddress().getCity()).isEqualTo("Kyiv");
    }

    @Test
    @DisplayName("Test findById() should return the accommodation by ID")
    public void findById_withExistingAccommodation_shouldReturnTrue() {
        Optional<Accommodation> accommodation =
                accommodationRepository.findById(TEST_VALID_ACCOMMODATION_ID);

        assertThat(accommodation).isPresent();
        assertThat(accommodation.get().getId()).isEqualTo(TEST_VALID_ACCOMMODATION_ID);
    }

    @Test
    @DisplayName("Test findById() should return empty for non-existing ID")
    public void findById_WithNonExistingId_shouldReturnEmpty() {
        Optional<Accommodation> accommodation =
                accommodationRepository.findById(INCORRECT_ACCOMMODATION_ID);

        assertThat(accommodation).isNotPresent();
    }
}
