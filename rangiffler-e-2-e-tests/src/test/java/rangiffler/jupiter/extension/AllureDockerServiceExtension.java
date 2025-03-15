package rangiffler.jupiter.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import rangiffler.config.Config;
import rangiffler.model.allure.AllureResults;
import rangiffler.model.allure.EncodedAllureResult;
import rangiffler.service.impl.AllureDockerApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
public class AllureDockerServiceExtension implements SuiteExtension {

    private static final Config CFG = Config.getInstance();

    private static final boolean inDocker = "docker".equals(System.getProperty("test.env"));
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Path pathToResults = Path.of("./rangiffler-e-2-e-tests/build/allure-results");

    private static final AllureDockerApiClient allureApiClient = new AllureDockerApiClient();

    @Override
    public void beforeSuite(ExtensionContext context) {
        if (inDocker) {
            allureApiClient.createProjectIfNotExist(CFG.projectId());
            allureApiClient.cleanResults(CFG.projectId());
        }
    }

    @Override
    public void afterSuite() {
        if (inDocker) {
            try (Stream<Path> allureResults = Files.walk(pathToResults).filter(Files::isRegularFile)) {
                for (Path path : allureResults.toList()) {
                    try (InputStream is = Files.newInputStream(path)) {
                        EncodedAllureResult encodedResult = new EncodedAllureResult(
                                encoder.encodeToString(is.readAllBytes()),
                                path.getFileName().toString()
                        );
                        allureApiClient.uploadResults(
                                CFG.projectId(),
                                new AllureResults(List.of(encodedResult)) // <-- ОТПРАВЛЯЕМ ПО ОДНОМУ ФАЙЛУ
                        );
                    }
                }
                allureApiClient.generateReport(CFG.projectId());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
