package util;

import config.DBConnect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadUtil {
    private static final Logger LOGGER = Logger.getLogger(UploadUtil.class.getName());
    private static String uploadDir;

    static {
        Properties properties = new Properties();
        String defaultDirWin = "C:/tea_pos_images";
        String defaultDirUnix = "/var/teapos_uploads/images/";

        try (InputStream input = UploadUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                uploadDir = DBConnect.resolvePlaceholder(properties.getProperty("upload.dir"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể đọc application.properties trong UploadUtil: " + e.getMessage());
        }

        if (uploadDir == null || uploadDir.trim().isEmpty()) {
            uploadDir = System.getProperty("os.name").toLowerCase().contains("win") ? defaultDirWin : defaultDirUnix;
        }

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private UploadUtil() {}

    public static String getUploadDir() {
        return uploadDir;
    }

    public static String uploadFile(HttpServletRequest request, String inputFieldName) {
        try {
            Part filePart = request.getPart(inputFieldName);
            if (filePart == null || filePart.getSize() == 0) {
                return null;
            }

            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                return null;
            }

            int dotIdx = fileName.lastIndexOf(".");
            if (dotIdx == -1) {
                return null;
            }

            String fileExt = fileName.substring(dotIdx).toLowerCase();
            // Validate extension
            if (!fileExt.matches("\\.(jpg|jpeg|png|webp|gif)$")) {
                LOGGER.warning("Định dạng file không được hỗ trợ: " + fileExt);
                return null;
            }

            String newFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExt;
            File file = new File(uploadDir, newFileName);
            filePart.write(file.getAbsolutePath());

            return request.getContextPath() + "/assets/images/" + newFileName;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi upload file: " + e.getMessage(), e);
        }
        return null;
    }
}
