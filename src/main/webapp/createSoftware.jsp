<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.projects.useraccessmanagementsystem.dao.Database" %>
<%@ page import="java.util.*" %>
<%
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    List<Map<String, String>> userList = new ArrayList<>();
    try {
        conn = Database.getConnection();
        String sql = "SELECT id, username, role FROM users";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            Map<String, String> user = new HashMap<>();
            user.put("id", rs.getString("id"));
            user.put("username", rs.getString("username"));
            user.put("role", rs.getString("role"));
            userList.add(user);
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
    <title>Create Software</title>
</head>
<body style="background-color: darkgrey;">
    <h2>Create New Software</h2>
    <form action="SoftwareServlet" method="post">
        <label for="softwareName">Software Name:</label>
        <input type="text" id="softwareName" name="name" required><br>

        <label for="description">Description:</label>
        <textarea id="description" name="description" required></textarea><br>

        <label>Access Levels:</label>
        <input type="checkbox" name="access_levels" value="Read"> Read
        <input type="checkbox" name="access_levels" value="Write"> Write
        <input type="checkbox" name="access_levels" value="Admin"> Admin<br>

        <button type="submit">Create Software</button>
    </form>

    <hr>

    <h2>Change User Role</h2>
    <form action="ChangeRole" method="post">
        <label for="user">Select User:</label>
        <select name="userId" id="user" required>
            <option value="">-- Select User --</option>
            <% for (Map<String, String> user : userList) { %>
                <option value="<%= user.get("id") %>">
                    <%= user.get("username") %> (Current Role: <%= user.get("role") %>)
                </option>
            <% } %>
        </select><br>

        <label>Change Role To:</label><br>
        <input type="radio" name="role" value="Employee" required> Employee<br>
        <input type="radio" name="role" value="Manager" required> Manager<br>
        <input type="radio" name="role" value="Admin" required> Admin<br>

        <button type="submit">Change Role</button>
    </form>
</body>
</html>
