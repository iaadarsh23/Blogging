package com.blog.services;

import com.blog.models.Blog;
import com.blog.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlogService {
    private static final Logger LOGGER = Logger.getLogger(BlogService.class.getName());

    public Blog findById(String id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Blog blog = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM blogs WHERE id = ?");
            stmt.setString(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                blog = mapResultSetToBlog(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding blog by id", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return blog;
    }
    
    public List<Blog> findAllByAuthorId(String authorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Blog> blogs = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM blogs WHERE author_id = ? ORDER BY created_at DESC");
            stmt.setString(1, authorId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                blogs.add(mapResultSetToBlog(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding blogs by author id", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return blogs;
    }
    
    public List<Blog> findAll() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Blog> blogs = new ArrayList<>();
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("SELECT * FROM blogs ORDER BY created_at DESC");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                blogs.add(mapResultSetToBlog(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all blogs", e);
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return blogs;
    }
    
    public Blog createBlog(Blog blog) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(
                "INSERT INTO blogs (id, title, content, author_id) VALUES (?, ?, ?, ?)");
            
            String blogId = UUID.randomUUID().toString();
            blog.setId(blogId);
            
            stmt.setString(1, blogId);
            stmt.setString(2, blog.getTitle());
            stmt.setString(3, blog.getContent());
            stmt.setString(4, blog.getAuthorId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating blog failed, no rows affected.");
            }
            
            return blog;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating blog", e);
            return null;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean updateBlog(Blog blog) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(
                "UPDATE blogs SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND author_id = ?");
            
            stmt.setString(1, blog.getTitle());
            stmt.setString(2, blog.getContent());
            stmt.setString(3, blog.getId());
            stmt.setString(4, blog.getAuthorId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating blog", e);
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    public boolean deleteBlog(String id, String authorId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement("DELETE FROM blogs WHERE id = ? AND author_id = ?");
            stmt.setString(1, id);
            stmt.setString(2, authorId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting blog", e);
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    private Blog mapResultSetToBlog(ResultSet rs) throws SQLException {
        Blog blog = new Blog();
        blog.setId(rs.getString("id"));
        blog.setTitle(rs.getString("title"));
        blog.setContent(rs.getString("content"));
        blog.setAuthorId(rs.getString("author_id"));
        blog.setCreatedAt(rs.getTimestamp("created_at"));
        blog.setUpdatedAt(rs.getTimestamp("updated_at"));
        return blog;
    }
    
    private void closeResources(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing PreparedStatement", e);
            }
        }
        
        DatabaseUtil.closeConnection(conn);
    }
}
