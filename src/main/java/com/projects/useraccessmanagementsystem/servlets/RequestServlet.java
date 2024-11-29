package com.projects.useraccessmanagementsystem.servlets;

import com.projects.useraccessmanagementsystem.dao.Database;
import com.projects.useraccessmanagementsystem.util.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/RequestServlet")
public class RequestServlet extends HttpServlet {
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

        //Check if the role is "Manager"
        if (role == null || role.equals("Manager")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to perform this operation");
            return;
        }

        int softwareId = Integer.parseInt(req.getParameter("software_id"));
        String accessType = req.getParameter("access_type");
        String reason = req.getParameter("reason");
        String username = (String) req.getSession().getAttribute("username");

        try (Connection conn = Database.getConnection()) {
            String userIdQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userIdQuery);
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            userRs.next();
            int userId = userRs.getInt("id");

            String sql = "INSERT INTO requests (user_id, software_id, access_type, reason) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, softwareId);
            stmt.setString(3, accessType);
            stmt.setString(4, reason);
            stmt.executeUpdate();
            resp.sendRedirect("requestAccess.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
