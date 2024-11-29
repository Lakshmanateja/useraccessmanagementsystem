<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body style="background-color: darkgrey;">
    <h2>Login</h2>
    <form action="LoginServlet" method="post">
        <input type="text" name="username" placeholder="Username" required />
        <input type="password" name="password" placeholder="Password" required />
        <button type="submit">Login</button>
    </form>
</body>
</html>