package com.projects.useraccessmanagementsystem.servlets;

import com.projects.useraccessmanagementsystem.dao.Database;
import com.projects.useraccessmanagementsystem.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try(Connection conn = Database.getConnection())
        {
            String sql = "SELECT password, role FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                String hashedPassword = rs.getString("password");
                String role = rs.getString("role");

                if(BCrypt.checkpw(password, hashedPassword)){
                    HttpSession session = req.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("role", role);

                    String token = JWTUtil.generateToken(username, role);
                    resp.setHeader("Authorization", "Bearer " + token);

                    switch(role){
                        case "Employee": resp.sendRedirect("requestAccess.jsp"); break;
                        case "Manager": resp.sendRedirect("pendingRequests.jsp"); break;
                        case "Admin": resp.sendRedirect("createSoftware.jsp"); break;
                    }
                }
                else
                {
                    resp.getWriter().write("Invalid credentials");
                }
            }
            else
            {
                resp.getWriter().write("User not found");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (ClassNotFoundException e){
            throw new RuntimeException(e);
        }
    }
}
