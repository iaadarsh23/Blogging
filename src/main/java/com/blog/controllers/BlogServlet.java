package com.blog.controllers;

import com.blog.models.Blog;
import com.blog.models.User;
import com.blog.services.BlogService;
import com.blog.services.UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/api/blogs/*")
public class BlogServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BlogService blogService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        blogService = new BlogService();
        userService = new UserService();
    }

    // Handle GET requests (fetch blogs)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Unauthorized\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Get a specific blog by ID
        if (pathInfo != null && pathInfo.length() > 1) {
            String blogId = pathInfo.substring(1);
            Blog blog = blogService.findById(blogId);
            
            if (blog != null) {
                out.write(convertBlogToJson(blog).toJSONString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Blog not found\"}");
            }
        } 
        // Get blogs by user
        else if (request.getServletPath().endsWith("/user")) {
            List<Blog> blogs = blogService.findAllByAuthorId(currentUser.getId());
            JSONArray jsonBlogs = new JSONArray();
            
            for (Blog blog : blogs) {
                jsonBlogs.add(convertBlogToJson(blog));
            }
            
            out.write(jsonBlogs.toJSONString());
        } 
        // Get all blogs
        else {
            List<Blog> blogs = blogService.findAll();
            JSONArray jsonBlogs = new JSONArray();
            
            for (Blog blog : blogs) {
                jsonBlogs.add(convertBlogToJson(blog));
            }
            
            out.write(jsonBlogs.toJSONString());
        }
    }

    // Handle POST requests (create blog)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Parse blog data from request
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        if (title == null || content == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Title and content are required\"}");
            return;
        }
        
        // Create and save blog
        Blog blog = new Blog();
        blog.setTitle(title);
        blog.setContent(content);
        blog.setAuthorId(currentUser.getId());
        
        Blog createdBlog = blogService.createBlog(blog);
        
        if (createdBlog != null) {
            response.getWriter().write(convertBlogToJson(createdBlog).toJSONString());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to create blog\"}");
        }
    }

    // Handle PUT requests (update blog)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Get blog ID from URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Blog ID is required\"}");
            return;
        }
        
        String blogId = pathInfo.substring(1);
        
        // Get blog from database
        Blog blog = blogService.findById(blogId);
        
        if (blog == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Blog not found\"}");
            return;
        }
        
        // Check if user is the author
        if (!blog.getAuthorId().equals(currentUser.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"You can only update your own blogs\"}");
            return;
        }
        
        // Parse updated blog data
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        
        if (title == null || content == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Title and content are required\"}");
            return;
        }
        
        // Update blog
        blog.setTitle(title);
        blog.setContent(content);
        
        boolean updated = blogService.updateBlog(blog);
        
        if (updated) {
            response.getWriter().write(convertBlogToJson(blog).toJSONString());
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to update blog\"}");
        }
    }

    // Handle DELETE requests (delete blog)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Check authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        
        // Get blog ID from URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Blog ID is required\"}");
            return;
        }
        
        String blogId = pathInfo.substring(1);
        
        // Get blog from database to check ownership
        Blog blog = blogService.findById(blogId);
        
        if (blog == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Blog not found\"}");
            return;
        }
        
        // Check if user is the author
        if (!blog.getAuthorId().equals(currentUser.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"You can only delete your own blogs\"}");
            return;
        }
        
        // Delete blog
        boolean deleted = blogService.deleteBlog(blogId, currentUser.getId());
        
        if (deleted) {
            response.getWriter().write("{\"success\":true}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Failed to delete blog\"}");
        }
    }

    // Helper method to convert Blog to JSON
    private JSONObject convertBlogToJson(Blog blog) {
        JSONObject jsonBlog = new JSONObject();
        jsonBlog.put("id", blog.getId());
        jsonBlog.put("title", blog.getTitle());
        jsonBlog.put("content", blog.getContent());
        jsonBlog.put("authorId", blog.getAuthorId());
        jsonBlog.put("createdAt", blog.getCreatedAt().getTime());
        jsonBlog.put("updatedAt", blog.getUpdatedAt().getTime());
        
        // Fetch author details
        User author = userService.findById(blog.getAuthorId());
        if (author != null) {
            JSONObject jsonAuthor = new JSONObject();
            jsonAuthor.put("id", author.getId());
            jsonAuthor.put("username", author.getUsername());
            jsonBlog.put("author", jsonAuthor);
        }
        
        return jsonBlog;
    }
}
