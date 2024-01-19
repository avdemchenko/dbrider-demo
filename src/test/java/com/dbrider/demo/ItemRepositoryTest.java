package com.dbrider.demo;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DBRider
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DBUnit(
        caseSensitiveTableNames = true
)
class ItemRepositoryTest extends AbstractDBTest {

    @Autowired
    ItemRepository itemRepository;

    @ParameterizedTest
    @DataSet(value = {"ItemRepositoryTest/items.xml"})
    @CsvSource({
            "hello, 1",
            "abc, 2",
            "xyz, "
    })
    void shouldFindByName(final String name, final Long expectedId) {
        // when
        final ItemEntity entity = itemRepository.findByName(name);

        // then
        if (expectedId == null) {
            assertThat(entity).isNull();
        } else {
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(expectedId);
            assertThat(entity.getName()).isEqualTo(name);
            assertThat(entity.getValue()).isNull();
        }
    }

    @Test
    @ExpectedDataSet(value = {
            "ItemRepositoryTest/items-after-insert.yml"
    })
    void shouldSaveEntity() {
        // given
        final ItemEntity entity = new ItemEntity();

        // when
        entity.setName("New Name");

        // and
        final ItemEntity savedEntity = itemRepository.save(entity);

        // then
        assertThat(savedEntity.getName()).isNotNull();
    }

    @Test
    @DataSet("ItemRepositoryTest/items.yml")
    @ExpectedDataSet("ItemRepositoryTest/items-after-update.yml")
    void shouldUpdateEntity() {
        // given
        final var entityOptional = itemRepository.findById(1L);
        assertThat(entityOptional).isPresent();

        // when
        final var updatedEntity = entityOptional.get();
        updatedEntity.setValue(42);

        // and
        itemRepository.saveAndFlush(updatedEntity);
    }
}
