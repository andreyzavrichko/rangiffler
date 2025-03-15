package rangiffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
    public static final String caseName = "Rangiffler backend logs";

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);
        addLogAttachment("Rangiffler-auth log", "./logs/rangiffler-auth/app.log");
        addLogAttachment("Rangiffler-geo log", "./logs/rangiffler-geo/app.log");
        addLogAttachment("Rangiffler-gateway log", "./logs/rangiffler-gateway/app.log");
        addLogAttachment("Rangiffler-photo log", "./logs/rangiffler-photo/app.log");
        addLogAttachment("Rangiffler-userdata log", "./logs/rangiffler-userdata/app.log");
        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    @SneakyThrows
    private void addLogAttachment(String attachmentName, String logPath) {
        Allure.getLifecycle().addAttachment(
                attachmentName,
                "text/html",
                ".log",
                Files.newInputStream(Path.of(logPath))
        );
    }
}