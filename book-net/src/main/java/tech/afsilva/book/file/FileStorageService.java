package tech.afsilva.book.file;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @NotNull MultipartFile file,
            @NotNull Integer userId) {
        final String fileUploadSubPath = "users"+ File.separator+userId;
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(
            @NotNull MultipartFile file,
            @NotNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + File.separator + fileUploadSubPath;
        File targetFile = new File(finalUploadPath);
        if (!targetFile.exists()) {
            boolean folderCreated = targetFile.mkdirs();
            if(!folderCreated){
                log.error("Could not create folder {} for file upload", finalUploadPath);
                return null;
            }
        }
        final String fileExtension = getFileExtension(file.getOriginalFilename());
        String targetFilePath = finalUploadPath+File.separator+System.currentTimeMillis()+"."+fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, file.getBytes());
            log.info("File {} saved successfully", targetFilePath);
            return targetFilePath;
        } catch (IOException e){
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return null;
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex < 0) {
            return "";
        }
        return originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }
}
