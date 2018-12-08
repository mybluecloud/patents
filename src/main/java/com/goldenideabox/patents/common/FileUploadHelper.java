package com.goldenideabox.patents.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadHelper {

    public static int upload(MultipartFile file, String path) {
        File tempFile = new File(path + File.separator
            + file.getOriginalFilename());

        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdirs();
        }
        if (!file.isEmpty()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));

                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return 2;
            }

        } else {
            return 1;
        }
        return 0;
    }

    public static int delete(String name) {
        File file = new File(name);

        if (!file.exists() || !file.isFile()) {
            return 1;
        }
        if (!file.delete()) {
            return 1;
        }

        return 0;

    }
}
