package com.blog.controllers;

import com.blog.models.User;
import com.blog.services.UserService;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        // Check authentication status
        if (pathInfo != null && pathInfo.equals("/status")) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("id", user.getId());
                jsonUser.put("username", user.getUsername());
                jsonUser.put("email", user.getEmail());
                
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("user", jsonUser);
                jsonResponse.put("authenticated", true);
                
                out.write(jsonResponse.toJSONString());
            } else {
                out.write("{\"authenticated\": false}");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        // Login
        if (pathInfo != null && pathInfo.equals("/login")) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            
            if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Username and password are required\"}");
                return;
            }
            
            if (userService.validateCredentials(username, password)) {
                User user = userService.findByUsername(username);
                
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("id", user.getId());
                jsonUser.put("username", user.getUsername());
                jsonUser.put("email", user.getEmail());
                
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("user", jsonUser);
                jsonResponse.put("authenticated", true);
                
                out.write(jsonResponse.toJSONString());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"error\":\"Invalid username or password\"}");
            }
        } 
        // Logout
        else if (pathInfo != null && pathInfo.equals("/logout")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            out.write("{\"success\":true}");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
