package io.example.integration.dsl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Slf4j
@Configuration
@EnableIntegration
public class IntegrationFlowConfig {

    public final String INPUT_DIR = "resources/input";
    public final String OUTPUT_DIR = "resources/output";
    public final String FILE_EXTENSION = ".txt";

    /**
     * Integration processes are constructed by composing endpoints into one or more message flows.
     * <p/>
     * By default, endpoints are automatically wired together with DirectChannel instances where the bean name
     * is based on the following pattern: <b>[IntegrationFlow.beanName].channel#[channelNameIndex]</b>.
     * <p/>
     * Example: <b>application.demoIntegrationFlow.channel#0</b> is the bean name of the channel instance connecting
     * the <b>MessageSource</b> and <b>Filter</b> endpoints.
     */
    @Bean
    public IntegrationFlow demoIntegrationFlow() {
        return IntegrationFlows.from(
                fileReadingMessageSource(),
                configurer -> configurer.poller(Pollers.fixedDelay(1000))
            )
            .filter(filetypeFilter())
            .handle(fileWritingMessageHandler())
            .get();
    }

    /**
     * The inbound channel adapter takes messages from an external messaging system and "adapts" it to
     * the Spring Integration Message type.
     * <p/>
     * Once a message comes in, via an inbound adapter, it flows from one component to another via channels.
     */
    @Bean(name = "fileReadingMessageSource2")
    public MessageSource<File> fileReadingMessageSource() {
        log.info("fileReadingMessageSource2 -> IntegrationFlowConfig::fileReadingMessageSource");

        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File(INPUT_DIR));

        return sourceReader;
    }

    /**
     * lambda interface for source type to accept
     */
    @Bean
    public GenericSelector<File> filetypeFilter() {
        // return new GenericSelector<File>() {
        //     @Override
        //     public boolean accept(final File source) {
        //         return source.getName().endsWith(FILE_EXTENSION);
        //     }
        // };

        return source -> source.getName().endsWith(FILE_EXTENSION);
    }

    /**
     * Creates a Service Activator that consumes the messages and produces an output
     */
    @Bean(name = "fileWritingMessageHandler2")
    public MessageHandler fileWritingMessageHandler() {
        log.info("fileWritingMessageHandler2 -> IntegrationFlowConfig::fileWritingMessageHandler");

        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));

        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);

        return handler;
    }
}
