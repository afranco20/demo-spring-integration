package io.example.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Slf4j
@Configuration
@EnableIntegration
public class IntegrationConfig {

    public final String INPUT_DIR = "resources/input";
    public final String OUTPUT_DIR = "resources/output";

    public final String FILE_PATTERN = "*.txt";

    @Bean
    public MessageChannel fileChannel() {
        return new DirectChannel();
    }

    /**
     * The inbound channel adapter takes messages from an external messaging system and "adapts" it to
     * the Spring Integration Message type.
     * <p/>
     * Once a message comes in, via an inbound adapter, it flows from one component to another via channels.
     */
    @Bean
    @InboundChannelAdapter(value = "fileChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource() {
        log.info("fileReadingMessageSource -> IntegrationConfig::fileReadingMessageSource");

        FileReadingMessageSource sourceReader = new FileReadingMessageSource();
        sourceReader.setDirectory(new File(INPUT_DIR));
        sourceReader.setFilter(new SimplePatternFileListFilter(FILE_PATTERN));

        return sourceReader;
    }

    /**
     * Creates a Service Activator that consumes the messages and produces an output
     */
    @Bean
    @ServiceActivator(inputChannel = "fileChannel")
    public MessageHandler fileWritingMessageHandler() {
        log.info("fileReadingMessageSource -> IntegrationConfig::fileWritingMessageHandler");

        FileWritingMessageHandler handler = new FileWritingMessageHandler(new File(OUTPUT_DIR));

        handler.setFileExistsMode(FileExistsMode.REPLACE);
        handler.setExpectReply(false);

        return handler;
    }
}
