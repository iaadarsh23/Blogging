import React, { createContext, useState, useEffect } from 'react';
import { userService } from '../services/mockService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Check if user is already logged in from localStorage
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      try {
        setCurrentUser(JSON.parse(storedUser));
      } catch (e) {
        console.error("Error parsing stored user:", e);
        localStorage.removeItem('currentUser');
      }
    }
    
    // Check auth status with mock backend
    const checkAuthStatus = async () => {
      try {
        const res = await userService.getCurrentUser();
        if (res.user) {
          setCurrentUser(res.user);
          localStorage.setItem('currentUser', JSON.stringify(res.user));
        }
      } catch (err) {
        console.error("Auth check failed:", err);
        // Don't clear currentUser here as we might be in offline mode
      } finally {
        setLoading(false);
      }
    };

    checkAuthStatus();
  }, []);

  const login = async (username, password) => {
    try {
      setError(null);
      // Use mock service for login
      const res = await userService.login(username, password);
      if (res.user) {
        setCurrentUser(res.user);
        localStorage.setItem('currentUser', JSON.stringify(res.user));
        return true;
      } else {
        setError("Login response missing user data");
        return false;
      }
    } catch (err) {
      console.error("Login error:", err);
      setError(err.message || "Login failed");
      return false;
    }
  };

  const register = async (username, email, password) => {
    try {
      setError(null);
      console.log("Attempting registration with mock service:", { username, email });
      
      // Use mock service for registration
      const res = await userService.register(username, email, password);
      if (res.user) {
        setCurrentUser(res.user);
        localStorage.setItem('currentUser', JSON.stringify(res.user));
        return true;
      } else {
        setError("Registration response missing user data");
        return false;
      }
    } catch (err) {
      console.error("Registration error:", err);
      setError(err.message || "Registration failed");
      return false;
    }
  };

  const logout = async () => {
    try {
      // No need to call API in mock mode
      setCurrentUser(null);
      localStorage.removeItem('currentUser');
    } catch (err) {
      console.error("Logout failed:", err);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        loading,
        error,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
