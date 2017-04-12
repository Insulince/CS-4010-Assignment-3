<!doctype html>
<html>
<head>
    <title>Start Session ${sessionCount}</title>
</head>
<body>
<center>
    <h1>Please Log in </h1>
    <hr/>
    <font size="+3">
        <form method="get" action="http://localhost:8080/js_test/sessionServlet">
            Your name: <input type="text" name="username"><br>
            Your password: <input type="password" name="password"><br>
            <input type="hidden" name="sessionString" value="no-session-string-yet"/>
            <input type="hidden" name="task" value="0">
            <input type="submit" value="Login">
        </form>
    </font>
</center>
</body>
</html>