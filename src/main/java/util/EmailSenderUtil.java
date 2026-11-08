package util;

import config.DBConnect;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =========================================================================
 * TEA POS ENTERPRISE - JAKARTA MAIL OTP SENDER
 * Compatible with Jakarta Mail 2.0+ (Angus Mail) and Gmail SMTP TLS.
 * =========================================================================
 */
public class EmailSenderUtil {
    private static final Logger LOGGER = Logger.getLogger(EmailSenderUtil.class.getName());
    private static final String SENDER_EMAIL;
    private static final String SENDER_PASSWORD;

    static {
        Properties properties = new Properties();
        String user = System.getenv("EMAIL_USER");
        String pass = System.getenv("EMAIL_PASSWORD");

        try (InputStream input = EmailSenderUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                String propUser = DBConnect.resolvePlaceholder(properties.getProperty("email.user"));
                String propPass = DBConnect.resolvePlaceholder(properties.getProperty("email.password"));

                if (propUser != null && !propUser.trim().isEmpty() && (user == null || user.trim().isEmpty())) {
                    user = propUser;
                }
                if (propPass != null && !propPass.trim().isEmpty() && (pass == null || pass.trim().isEmpty())) {
                    pass = propPass;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Không thể nạp application.properties trong EmailSenderUtil: " + e.getMessage());
        }

        if (user == null || user.trim().isEmpty() || user.contains("${")) {
            user = "";
        }
        if (pass == null || pass.trim().isEmpty() || pass.contains("${")) {
            pass = "";
        }

        SENDER_EMAIL = user.trim();
        SENDER_PASSWORD = pass.replace(" ", "").trim();
        LOGGER.info("[TEA POS] Email Sender cấu hình tài khoản: " + SENDER_EMAIL);
    }

    private EmailSenderUtil() {}

    public static boolean sendOTPEmail(String recipientEmail, String otpCode) {
        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "TEA POS Hệ Thống Quán"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail.trim()));
            message.setSubject("MÃ XÁC THỰC OTP - HỆ THỐNG TEA POS");

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;'>"
                    + "<div style='background-color: #059669; color: white; padding: 24px; text-align: center;'>"
                    + "<h1 style='margin: 0; font-size: 24px;'>TEA POS CHÀO BẠN!</h1>"
                    + "</div>"
                    + "<div style='padding: 30px; background-color: #ffffff;'>"
                    + "<p style='font-size: 16px; color: #334155;'>Xin chào bạn,</p>"
                    + "<p style='font-size: 15px; color: #475569;'>Mã xác thực bảo mật một lần (OTP) của bạn là:</p>"
                    + "<div style='text-align: center; margin: 25px 0;'>"
                    + "<span style='display: inline-block; background-color: #ecfdf5; color: #059669; border: 2px dashed #059669; font-size: 32px; font-weight: bold; letter-spacing: 8px; padding: 12px 32px; border-radius: 8px;'>"
                    + otpCode + "</span>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #64748b;'>Mã xác thực có hiệu lực trong vòng <b>5 phút</b>. Vui lòng tuyệt đối không chia sẻ mã này cho bất kỳ ai.</p>"
                    + "<hr style='border: none; border-top: 1px solid #f1f5f9; margin: 20px 0;'>"
                    + "<p style='font-size: 12px; color: #94a3b8; text-align: center;'>Email này được gửi tự động từ hệ thống TEA POS.</p>"
                    + "</div>"
                    + "</div>";

            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            LOGGER.info("[TEA POS] Đã gửi mã OTP thành công tới: " + recipientEmail);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gửi email OTP tới: " + recipientEmail + " -> " + e.getMessage(), e);
            return false;
        }
    }
}
