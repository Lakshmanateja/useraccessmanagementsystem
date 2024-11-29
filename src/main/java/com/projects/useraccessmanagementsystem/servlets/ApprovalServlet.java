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

@WebServlet("/ApprovalServlet")
public class ApprovalServlet extends HttpServlet {
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

        //Check if the role is "Employee"
        if (role == null || role.equals("Employee")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to perform this operation");
            return;
        }

        int requestId = Integer.parseInt(req.getParameter("request_id"));
        String action = req.getParameter("action");

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE requests SET status = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, action.equals("approve") ? "Approved" : "Rejected");
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
            resp.sendRedirect("pendingRequests.jsp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
