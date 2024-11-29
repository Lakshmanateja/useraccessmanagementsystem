package com.projects.useraccessmanagementsystem.servlets;

import com.projects.useraccessmanagementsystem.dao.Database;
import com.projects.useraccessmanagementsystem.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/ChangeRole")
public class ChangeRole extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Remove the "Bearer " prefix

        Claims claims;
        try {
            //Validate the token and extract claims
            claims = JWTUtil.validateToken(token);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }

        //Retrieve the role from the claims
        String role = claims.get("role", String.class); // Extract the role from the claims

        //Check if the role is "Employee" or "Manager"
        if (role == null || !role.equals("Admin")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to perform this operation");
            return;
        }

        String userIdParam = req.getParameter("userId");
        int userId = Integer.parseInt(userIdParam);
        String newRole = req.getParameter("role");
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = Database.getConnection();

            String sql = "UPDATE users SET role = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                req.setAttribute("message", "Role updated successfully!");
            } else {
                req.setAttribute("message", "Failed to update role. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("message", "An error occurred: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher("createSoftware.jsp");
        dispatcher.forward(req, resp);
    }
}
