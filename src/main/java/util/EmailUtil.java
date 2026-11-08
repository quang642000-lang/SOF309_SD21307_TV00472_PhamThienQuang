package util;

public class EmailUtil {
    private EmailUtil() {}

    public static boolean sendEmail(String toEmail, String otp) {
        return EmailSenderUtil.sendOTPEmail(toEmail, otp);
    }
}
