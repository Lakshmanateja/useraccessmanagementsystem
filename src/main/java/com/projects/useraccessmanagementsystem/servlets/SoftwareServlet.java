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
import java.sql.SQLException;

@WebServlet("/SoftwareServlet")
public class SoftwareServlet extends HttpServlet {
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

        String name = req.getParameter("name");
        String description = req.getParameter("description");
        String[] accessLevels = req.getParameterValues("access_levels");

        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO software (name, description, access_levels) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setString(3, String.join(",", accessLevels));
            stmt.executeUpdate();
            resp.sendRedirect("createSoftware.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
