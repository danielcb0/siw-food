package it.uniroma3.siw_food.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UploadFileService {
    private final Path rootLocation = Paths.get("src/main/resources/static/images");

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();

            // Check if the file already exists and append a number to the filename if it does
            int counter = 1;
            while (Files.exists(destinationFile)) {
                String newFilename = addSuffix(file.getOriginalFilename(), counter);
                destinationFile = this.rootLocation.resolve(Paths.get(newFilename))
                        .normalize().toAbsolutePath();
                counter++;
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile);
            }
            return destinationFile.getFileName().toString();
        } catch (IOException e) {
            throw new RuntimeException("FAIL!", e);
        }
    }

    private String addSuffix(String filename, int counter) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return filename + "_" + counter;
        } else {
            return filename.substring(0, dotIndex) + "_" + counter + filename.substring(dotIndex);
        }
    }

}
