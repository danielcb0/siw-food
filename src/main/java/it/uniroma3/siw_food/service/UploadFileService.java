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
            String filename = file.getOriginalFilename();
            System.out.println("Filename: " + filename); // Imprimir el nombre del archivo para depuración
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("FAIL!", e);
        }
    }

}
