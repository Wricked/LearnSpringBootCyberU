package com.learnspringboot.learningspringbootvideo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Victor
 * Date 16/11/2021 12:17
 * @project learning-spring-boot-video
 */

@Service
public class ImageService {

    private static final String UPLOAD_ROOT = "upload-dir";

    private final ImageRepository repository;
    @Autowired
    private final ResourceLoader loader;
    private final Counter counter;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository, ResourceLoader loader, MeterRegistry registry, SimpMessagingTemplate messagingTemplate, UserRepository userRepository) {
        this.repository = imageRepository;
        this.loader = loader;
        this.counter = Counter.builder("files.uploaded").register(registry);
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    public Page<Image> findPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Resource findOneImage(String fileName) {
        return loader.getResource("file:" + UPLOAD_ROOT + "/" + fileName);
    }

    public void createImage (MultipartFile file) throws IOException {
        if(!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_ROOT, file.getOriginalFilename()));
            repository.save(new Image(file.getOriginalFilename(), userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())));
            counter.increment();
            Metrics.gauge("files.uploaded.getBytes", file.getSize());
            messagingTemplate.convertAndSend("/topic/newImage", file.getOriginalFilename());
        }
    }

    @PreAuthorize("@imageRepository.findByName(#fileName)?.owner?.username == authentication?.name or hasRole('ADMIN')")
    public void deleteImage(@Param("fileName") String fileName) throws IOException {
        final Image byName = repository.findByName(fileName);
        repository.delete(byName);
        Files.deleteIfExists(Paths.get(UPLOAD_ROOT, fileName));
        messagingTemplate.convertAndSend("/topic/deleteImage", fileName);
    }

    @Bean
    CommandLineRunner setUp (ImageRepository imageRepository, UserRepository userRepository) throws IOException {
        return (args) -> {
            FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
            Files.createDirectory(Paths.get(UPLOAD_ROOT));

            User victor = userRepository.save(new User("victor", "123", "ROLE_ADMIN","ROLE_USER"));
            User user = userRepository.save(new User("user", "123", "ROLE_USER"));

            FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/test"));
            repository.save(new Image("test", victor));

            FileCopyUtils.copy("Test file2", new FileWriter(UPLOAD_ROOT + "/test2"));
            repository.save(new Image("test2", victor));

            FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/test3"));
            repository.save(new Image("test3", victor));
        };
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> addPersonRegistry() {
        return registry -> registry.config().namingConvention().name("files.uploaded.lastBytes", Meter.Type.COUNTER);
    }
}
