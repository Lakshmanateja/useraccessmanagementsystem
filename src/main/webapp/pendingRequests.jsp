<%@ page import="java.sql.*" %>
<%@ page import="javax.sql.DataSource" %>
<%@ page import="com.projects.useraccessmanagementsystem.dao.Database" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    List<Map<String, String>> requestList = new ArrayList<>();
    try {
        conn = Database.getConnection();
        String sql = "SELECT r.id, u.username, s.name AS software_name, r.access_type, r.reason " +
                     "FROM requests r " +
                     "JOIN users u ON r.user_id = u.id " +
                     "JOIN software s ON r.software_id = s.id " +
                     "WHERE r.status = 'Pending'";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
            Map<String, String> requestData = new HashMap<>();
            requestData.put("id", rs.getString("id"));
            requestData.put("username", rs.getString("username"));
            requestData.put("software_name", rs.getString("software_name"));
            requestData.put("access_type", rs.getString("access_type"));
            requestData.put("reason", rs.getString("reason"));
            requestList.add(requestData);
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
    <title>Pending Requests</title>
</head>
<body style="background-color: darkgrey;">
    <h2>Pending Access Requests</h2>
    <form action="ApprovalServlet" method="post">
        <table border="1">
            <thead>
                <tr>
                    <th>Employee Name</th>
                    <th>Software</th>
                    <th>Access Type</th>
                    <th>Reason</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map<String, String> requestItem : requestList) { %>
                    <tr>
                        <td><%= requestItem.get("username") %></td>
                        <td><%= requestItem.get("software_name") %></td>
                        <td><%= requestItem.get("access_type") %></td>
                        <td><%= requestItem.get("reason") %></td>
                        <td>
                            <input type="hidden" name="request_id" value="<%= requestItem.get("id") %>">
                            <select name="action" required>
                                <option value="">-- Select Action --</option>
                                <option value="Approved">Approve</option>
                                <option value="Rejected">Reject</option>
                            </select>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
        <button type="submit">Submit Decisions</button>
    </form>
</body>
</html>
