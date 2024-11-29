<!DOCTYPE html>
<html>
<head>
    <title>Sign Up</title>
</head>
<body style="background-color: darkgrey;">
    <h2>Sign Up</h2>
    <form action="SignUpServlet" method="post">
        <input type="text" name="username" placeholder="Username" required />
        <input type="password" name="password" placeholder="Password" required />
        <input type="hidden" name="role" value="Employee" />
        <button type="submit">Sign Up</button>
    </form>
</body>
</html>
