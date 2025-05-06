// mockService.js - Simulates backend API calls with localStorage

// Mock database tables
const DB_KEYS = {
  USERS: 'mock_users',
  BLOGS: 'mock_blogs',
  COMMENTS: 'mock_comments'
};

// Initialize mock database if it doesn't exist
function initMockDB() {
  if (!localStorage.getItem(DB_KEYS.USERS)) {
    localStorage.setItem(DB_KEYS.USERS, JSON.stringify([]));
  }
  
  if (!localStorage.getItem(DB_KEYS.BLOGS)) {
    localStorage.setItem(DB_KEYS.BLOGS, JSON.stringify([]));
  }
  
  if (!localStorage.getItem(DB_KEYS.COMMENTS)) {
    localStorage.setItem(DB_KEYS.COMMENTS, JSON.stringify([]));
  }
}

// Helper to generate unique IDs
function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2, 5);
}

// User related functions
export const userService = {
  // Register a new user
  register: async (username, email, password) => {
    return new Promise((resolve, reject) => {
      // Simulate network delay
      setTimeout(() => {
        try {
          initMockDB();
          
          // Get existing users
          const users = JSON.parse(localStorage.getItem(DB_KEYS.USERS));
          
          // Check if username already exists
          if (users.some(user => user.username === username)) {
            return reject({ message: 'Username already exists' });
          }
          
          // Create new user
          const newUser = {
            id: generateId(),
            username,
            email,
            password, // In a real app, this would be hashed
            createdAt: new Date().toISOString()
          };
          
          // Add to "database"
          users.push(newUser);
          localStorage.setItem(DB_KEYS.USERS, JSON.stringify(users));
          
          // Return user data (without password)
          const { password: _, ...userWithoutPassword } = newUser;
          resolve({ user: userWithoutPassword, success: true });
        } catch (error) {
          reject({ message: 'Registration failed', error });
        }
      }, 800); // Simulate network delay
    });
  },
  
  // Login user
  login: async (username, password) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          initMockDB();
          
          // Get users
          const users = JSON.parse(localStorage.getItem(DB_KEYS.USERS));
          
          // Find matching user
          const user = users.find(u => 
            u.username === username && u.password === password
          );
          
          if (user) {
            // Return user data (without password)
            const { password: _, ...userWithoutPassword } = user;
            resolve({ user: userWithoutPassword, authenticated: true });
          } else {
            reject({ message: 'Invalid username or password' });
          }
        } catch (error) {
          reject({ message: 'Login failed', error });
        }
      }, 800);
    });
  },
  
  // Get current user
  getCurrentUser: async () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        const currentUser = localStorage.getItem('currentUser');
        if (currentUser) {
          resolve({ user: JSON.parse(currentUser), authenticated: true });
        } else {
          resolve({ authenticated: false });
        }
      }, 300);
    });
  }
};

// Blog related functions
export const blogService = {
  // Get all blogs
  getAllBlogs: async () => {
    return new Promise((resolve) => {
      setTimeout(() => {
        initMockDB();
        const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
        resolve(blogs);
      }, 800);
    });
  },
  
  // Get blog by ID
  getBlogById: async (id) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        initMockDB();
        const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
        const blog = blogs.find(b => b.id === id);
        
        if (blog) {
          resolve(blog);
        } else {
          reject({ message: 'Blog not found' });
        }
      }, 500);
    });
  },
  
  // Create a new blog
  createBlog: async (blogData) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          initMockDB();
          
          // Get current user
          const currentUser = JSON.parse(localStorage.getItem('currentUser'));
          if (!currentUser) {
            return reject({ message: 'You must be logged in to create a blog' });
          }
          
          // Get existing blogs
          const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
          
          // Create new blog
          const newBlog = {
            id: generateId(),
            title: blogData.title,
            content: blogData.content,
            authorId: currentUser.id,
            authorName: currentUser.username,
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
          };
          
          // Add to "database"
          blogs.push(newBlog);
          localStorage.setItem(DB_KEYS.BLOGS, JSON.stringify(blogs));
          
          resolve(newBlog);
        } catch (error) {
          reject({ message: 'Failed to create blog', error });
        }
      }, 800);
    });
  },
  
  // Update a blog
  updateBlog: async (id, blogData) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          initMockDB();
          
          // Get current user
          const currentUser = JSON.parse(localStorage.getItem('currentUser'));
          if (!currentUser) {
            return reject({ message: 'You must be logged in to update a blog' });
          }
          
          // Get existing blogs
          const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
          
          // Find blog to update
          const blogIndex = blogs.findIndex(b => b.id === id);
          
          if (blogIndex === -1) {
            return reject({ message: 'Blog not found' });
          }
          
          // Check if user is the author
          if (blogs[blogIndex].authorId !== currentUser.id) {
            return reject({ message: 'You can only edit your own blogs' });
          }
          
          // Update blog
          blogs[blogIndex] = {
            ...blogs[blogIndex],
            ...blogData,
            updatedAt: new Date().toISOString()
          };
          
          // Save to "database"
          localStorage.setItem(DB_KEYS.BLOGS, JSON.stringify(blogs));
          
          resolve(blogs[blogIndex]);
        } catch (error) {
          reject({ message: 'Failed to update blog', error });
        }
      }, 800);
    });
  },
  
  // Delete a blog
  deleteBlog: async (id) => {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        try {
          initMockDB();
          
          // Get current user
          const currentUser = JSON.parse(localStorage.getItem('currentUser'));
          if (!currentUser) {
            return reject({ message: 'You must be logged in to delete a blog' });
          }
          
          // Get existing blogs
          const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
          
          // Find blog to delete
          const blog = blogs.find(b => b.id === id);
          
          if (!blog) {
            return reject({ message: 'Blog not found' });
          }
          
          // Check if user is the author
          if (blog.authorId !== currentUser.id) {
            return reject({ message: 'You can only delete your own blogs' });
          }
          
          // Filter out the blog to delete
          const updatedBlogs = blogs.filter(b => b.id !== id);
          
          // Save to "database"
          localStorage.setItem(DB_KEYS.BLOGS, JSON.stringify(updatedBlogs));
          
          resolve({ success: true });
        } catch (error) {
          reject({ message: 'Failed to delete blog', error });
        }
      }, 800);
    });
  }
};

// Add some sample data for testing
export function addSampleData() {
  initMockDB();
  
  // Add sample users if none exist
  const users = JSON.parse(localStorage.getItem(DB_KEYS.USERS));
  if (users.length === 0) {
    const sampleUsers = [
      {
        id: 'user1',
        username: 'johndoe',
        email: 'john@example.com',
        password: 'password123',
        createdAt: new Date().toISOString()
      },
      {
        id: 'user2',
        username: 'janedoe',
        email: 'jane@example.com',
        password: 'password123',
        createdAt: new Date().toISOString()
      }
    ];
    localStorage.setItem(DB_KEYS.USERS, JSON.stringify(sampleUsers));
  }
  
  // Add sample blogs if none exist
  const blogs = JSON.parse(localStorage.getItem(DB_KEYS.BLOGS));
  if (blogs.length === 0) {
    const sampleBlogs = [
      {
        id: 'blog1',
        title: 'Getting Started with React',
        content: 'React is a JavaScript library for building user interfaces. It allows you to create reusable UI components and manage state efficiently.',
        authorId: 'user1',
        authorName: 'johndoe',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      },
      {
        id: 'blog2',
        title: 'Java Servlets: A Comprehensive Guide',
        content: 'Java Servlets are server-side components that extend the capabilities of a server. They are commonly used to handle HTTP requests and generate dynamic content.',
        authorId: 'user2',
        authorName: 'janedoe',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
    ];
    localStorage.setItem(DB_KEYS.BLOGS, JSON.stringify(sampleBlogs));
  }
}

// Initialize with sample data
addSampleData();
