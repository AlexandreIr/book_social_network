package tech.afsilva.book.file;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {
    public static byte[] readFileFromLocation(String fileURL) {
        if(StringUtils.isBlank(fileURL)){
            return null;
        }

        try{
            Path filePath = new File(fileURL).toPath();
            return Files.readAllBytes(filePath);
        } catch (IOException e){
            log.warn("No file found in the path {}", fileURL);
        }

        return null;
    }
}
