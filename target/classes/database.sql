-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS blogging_platform;
USE blogging_platform;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create blogs table
CREATE TABLE IF NOT EXISTS blogs (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert some sample users
INSERT INTO users (id, username, email, password) VALUES
('1', 'johndoe', 'john@example.com', 'password123'),
('2', 'janedoe', 'jane@example.com', 'password123');

-- Insert some sample blogs
INSERT INTO blogs (id, title, content, author_id) VALUES
('1', 'Getting Started with Java Servlets', 'Java Servlets are server-side components that handle requests and generate responses. This blog will guide you through the basics of servlet development...', '1'),
('2', 'React Hooks Explained', 'React Hooks allow you to use state and other React features without writing a class. In this blog post, we will explore the most commonly used hooks...', '1'),
('3', 'Building Modern APIs with Spring Boot', 'Spring Boot makes it easy to create stand-alone, production-grade Spring-based Applications. In this blog, we will see how to build RESTful APIs...', '2');
