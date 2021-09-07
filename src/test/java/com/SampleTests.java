package com;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.time.Duration.ofMillis;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertTimeout;


/** SpringFramework.Test & JUnit 5 NOTES

    @SpringBootTest : Loads the complete Spring application context. Used for an integration tests.
    @ExtendWith(SpringExtension.class) : Used to tell JUnit 5 to enable Spring support.

    @ActiveProfiles("profileName") : Used to select test profile.

    @Sql(scripts = "classpath:data/data.sql") : Used to run sql file that under resources package before test.

    @Disabled : Used to disable test.

    @TestMethodOrder : Used to configure the test method execution order. Parameters:
        --> MethodOrderer.OrderAnnotation.class : sorts test methods numerically based on values specified via the @Order annotation.
        --> MethodOrderer.Random.class : sorts test methods randomly
        --> MethodOrderer.Alphanumeric.class : sorts test methods alphanumerically based on their method name and formal parameter lists.
    @Order : Used to set test order for OrderAnnotation

    @BeforeEach : Used to executed before each @Test, @RepeatedTest, @ParameterizedTest, or @TestFactory method in the current class.
    @AfterEach : Used to executed after each @Test, @RepeatedTest, @ParameterizedTest, or @TestFactory method in the current class.
    @BeforeAll : Used to executed before all @Test, @RepeatedTest, @ParameterizedTest, or @TestFactory method in the current class.
    @AfterAll :  Used to executed after all @Test, @RepeatedTest, @ParameterizedTest, or @TestFactory method in the current class.

    @DisplayName : Declares a custom display name.

    @RepeatedTest : The test runs for the given number of repetitions.

    @ParameterizedTest : Used for running parameterized test
    @ValueSource : Used to specify the parameters of the parameterized test.

    @TempDir : Used to create and clean up a temporary directory for an individual test or all tests in a test class.

    @Nested : Used to group tests and express the relationship among groups of tests.

    @EnabledOnOs : A test may be enabled on a particular operating system.
    @DisabledOnOs : A test may be disabled on a particular operating system.

    @EnabledOnJre : A test may be enabled on a particular versions of the JRE.
    @DisabledOnJre : A test may be disabled on a particular versions of the JRE.

    @EnabledIfEnvironmentVariable : A test may be enabled based on the value of the named environment variable from the underlying OS.
    @DisabledIfEnvironmentVariable :  A test may be disabled based on the value of the named environment variable from the underlying OS.

    - import org.junit.Assert; --> JUnit4 Assert (You can use in Junit5)
    - import org.junit.runner.RunWith; --> In JUnit4. Use @ExtendWith in Junit5 instead of @RunWith
    - import org.springframework.test.context.junit4.SpringRunner; --> Use SpringRunner.class with @RunWith in JUnit4 instead of SpringExtension in Junit5.

 **/

//@SpringBootTest
//@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Tag("sample")
public class SampleTests {

    @BeforeEach
    public void beforeEach(TestInfo testInfo) {
        System.out.println(testInfo.getTestMethod().get().getName() + " started.");
    }

    @AfterEach
    public void afterEach(TestInfo testInfo) {
        System.out.println(testInfo.getTestMethod().get().getName() + " completed.");
    }

    @Test
    @Tag("testInfo-test")
    @DisplayName("printTestInfo-test")
    public void printTestInfo(TestInfo testInfo) {
        System.out.printf("Test method display name: %s%n", testInfo.getDisplayName());
        System.out.printf("Test method name: %s%n", testInfo.getTestMethod().get().getName());
        System.out.printf("Full path test method name with parameters: %s%n", testInfo.getTestMethod().get());
        System.out.printf("Test class name: %s%n", testInfo.getTestClass().get().getSimpleName());
        System.out.printf("Test tags: %s%n", testInfo.getTags());
    }

    @RepeatedTest(2)
    @DisplayName("repeated-test")
    public void repeatedTest(TestInfo testInfo, RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        int totalRepetitions = repetitionInfo.getTotalRepetitions();
        String methodName = testInfo.getTestMethod().get().getName();
        System.out.println(String.format("The repeated test ran. Repetition %d of %d for %s", currentRepetition, totalRepetitions, methodName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"param1", "param2"})
    @DisplayName("parameterized-test")
    void parameterizedTest(String param) {
        System.out.println(String.format("The parameterized test ran. Paramater: %s", param));
    }

    @Test
    @DisplayName("writeItemsToFile-test")
    void writeItemsToFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.write(file, singletonList(String.join(",", "a", "b", "c"))); // String... elements
        Assertions.assertEquals(singletonList("a,b,c"), Files.readAllLines(file));
    }

    @Test
    @DisplayName("timeout-test")
    void failsIfExecutionTimeExceeds100Milliseconds() {
        assertTimeout(ofMillis(100), () -> {
            System.out.println("The test will fail due to timeout.");
            Thread.sleep(1000);
        });
    }

    @Nested
    @DisplayName("nested-test")
    class nestedTest {

        @Test
        @DisplayName("insideNested-test")
        public void insideNested() {
            System.out.println("The test ran inside nested test");
        }

    }

    @Test
    @DisplayName("printSystemEnv-test")
    public void printSystemEnv() {
        System.out.println(System.getenv().toString());
    }

    @Test
    @EnabledOnOs({OS.WINDOWS, OS.LINUX})
    @DisplayName("enabledOnOs-test")
    public void onWindowsOrLinux() {
        System.out.println("The test ran on Windows or Linux");
    }

    @Test
    @DisabledOnOs(OS.MAC)
    @DisplayName("disabledOnOs-test")
    public void notOnMac() {
        System.out.println("The test ran on except Mac");
    }

    @Test
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_9})
    @DisplayName("enabledOnJre-test")
    void onJava8Or9() {
        System.out.println("The test ran on Java 8 or Java 9");
    }

    @Test
    @DisabledOnJre({JRE.JAVA_10})
    @DisplayName("disabledOnJre-test")
    void notOnJava10() {
        System.out.println("The test ran on except Java 10.");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")
    @DisplayName("enabledIfEnvironmentVariable-test")
    void onlyOnStagingServer() {
        System.out.println("The test ran on given environment variable");
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "ENV", matches = ".*development.*")
    @DisplayName("disabledIfEnvironmentVariable-test")
    void notOnDeveloperWorkstation() {
        System.out.println("The test ran on except given environment variable");
    }

}
