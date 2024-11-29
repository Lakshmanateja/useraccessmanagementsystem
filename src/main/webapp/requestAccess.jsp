<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="java.util.*" %>
<%@ page import="com.projects.useraccessmanagementsystem.dao.Database" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    List<Map<String, String>> softwareList = new ArrayList<>();
    try {
        conn = Database.getConnection();
        String sql = "SELECT id, name FROM software";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            Map<String, String> software = new HashMap<>();
            software.put("id", rs.getString("id"));
            software.put("name", rs.getString("name"));
            softwareList.add(software);
        }
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();
        if (conn != null) conn.close();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Request Access</title>
</head>
<body style="background-color: darkgrey;">
    <h2>Request Access to Software</h2>
    <form action="RequestServlet" method="post">
        <label for="software">Select Software:</label>
        <select name="software_id" id="software" required>
            <option value="">-- Select Software --</option>
            <% for (Map<String, String> software : softwareList) { %>
                <option value="<%= software.get("id") %>"><%= software.get("name") %></option>
            <% } %>
        </select>

        <label for="accessType">Select Access Type:</label>
        <select name="access_type" id="accessType" required>
            <option value="">-- Select Access Type --</option>
            <option value="Read">Read</option>
            <option value="Write">Write</option>
            <option value="Admin">Admin</option>
        </select>

        <label for="reason">Reason for Request:</label>
        <textarea name="reason" id="reason" required></textarea>

        <button type="submit">Submit Request</button>
    </form>
</body>
</html>
