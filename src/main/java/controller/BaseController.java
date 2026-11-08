package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ApiResponse;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * BaseController provides standardized methods for handling HTTP requests,
 * reading JSON bodies, and dispatching robust, escaped JSON responses.
 */
public abstract class BaseController extends HttpServlet {
    protected static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .create();

    protected void sendJson(HttpServletResponse response, Object obj) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(obj));
    }

    protected void sendSuccess(HttpServletResponse response, String message, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        sendJson(response, ApiResponse.success(message, data));
    }

    protected void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        sendJson(response, ApiResponse.success(data));
    }

    protected void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        ApiResponse<Void> errorResp = ApiResponse.error(message);
        response.getWriter().write(gson.toJson(errorResp));
    }

    protected String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
