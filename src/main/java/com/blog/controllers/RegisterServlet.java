package com.blog.controllers;

import com.blog.models.User;
import com.blog.services.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("RegisterServlet: Processing registration request");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = null;
        String email = null;
        String password = null;
        
        String contentType = request.getContentType();
        System.out.println("RegisterServlet: Content-Type: " + contentType);
        
        // Check content type to determine if this is a form submission or JSON request
        if (contentType != null && contentType.contains("application/json")) {
            // Parse JSON from request body
            try {
                BufferedReader reader = request.getReader();
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                
                String jsonContent = jsonBuilder.toString();
                System.out.println("RegisterServlet: JSON content: " + jsonContent);
                
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
                
                username = (String) jsonObject.get("username");
                email = (String) jsonObject.get("email");
                password = (String) jsonObject.get("password");
            } catch (ParseException e) {
                System.out.println("RegisterServlet: JSON parsing error: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Invalid JSON format: " + e.getMessage() + "\"}");
                return;
            }
        } else {
            // Get parameters from form submission
            username = request.getParameter("username");
            email = request.getParameter("email");
            password = request.getParameter("password");
        }
        
        System.out.println("RegisterServlet: Username: " + username + ", Email: " + email);
        
        // Validate input
        if (username == null || email == null || password == null || 
            username.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("RegisterServlet: Missing required fields");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"All fields are required\"}");
            return;
        }
        
        // Check if username already exists
        User existingUser = userService.findByUsername(username);
        if (existingUser != null) {
            System.out.println("RegisterServlet: Username already exists");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write("{\"error\":\"Username already exists\"}");
            return;
        }
        
        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // In a real app, password should be hashed
        
        System.out.println("RegisterServlet: Attempting to create user: " + username);
        
        User createdUser = userService.createUser(newUser);
        
        if (createdUser != null) {
            System.out.println("RegisterServlet: User created successfully: " + createdUser.getId());
            // Set user in session
            HttpSession session = request.getSession();
            session.setAttribute("user", createdUser);
            
            // Return success JSON
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("id", createdUser.getId());
            jsonUser.put("username", createdUser.getUsername());
            jsonUser.put("email", createdUser.getEmail());
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("user", jsonUser);
            jsonResponse.put("success", true);
            
            out.write(jsonResponse.toJSONString());
        } else {
            System.out.println("RegisterServlet: User creation failed (returned null from UserService)");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Registration failed, see backend logs for details\"}");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}
