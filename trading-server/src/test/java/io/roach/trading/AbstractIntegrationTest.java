package io.roach.trading;

import io.roach.trading.doubles.DoublesService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({ProfileNames.CRDB_LOCAL})
//@ActiveProfiles({ProfileNames.CRDB_LOCAL, ProfileNames.VERBOSE})
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
public abstract class AbstractIntegrationTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DoublesService doublesService;
}
