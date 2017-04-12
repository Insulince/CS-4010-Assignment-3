<%--@elvariable id="notes" type="assignment3.beans.NotesBean"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>
<head>
    <title>Update Notes ${sessionCount}</title>
    <style>
        div.scrolling {
            background-color: #FFFFFF;
            width: 1100px;
            height: 400px;
            overflow: scroll;
        }
    </style>
</head>
<body>
<%--Parameter sessionString: ${requestScope.sessionString}--%>
<center><h1> Select a Version</h1></center>
<hr/>
<font size="+1">
    <c:set var="this_version" scope="page" value="${notes.versionId}"/>
    <c:if test="${this_version == 0}">
    <c:set var="this_version" value=""/><p>
    </c:if>
    <form method="get" action="http://localhost:8080/js_test/sessionServlet">
        Java Source: <input type="text" name="javaSource" value="${notes.fileName}">&nbsp;&nbsp;&nbsp;Version: <input type="text" name="version" value="${this_version}">
        <br>
        <input type="hidden" name="sessionString" value="${requestScope.sessionString}">
        <input type="hidden" name="task" value="1">
        <input type="submit" value="Submit">
    </form>
    <hr/>
    <form method="get" action="http://localhost:8080/js_test/sessionServlet" target="_blank">
        <center>
            <h2>Notes: ${notes.fileName} ${this_version}</h2>
        </center>
        <textarea rows="5" cols="100" name="notes">
            ${notes.notes}
        </textarea> <br>
        <input type="hidden" name="sessionString" value="${requestScope.sessionString}">
        <input type="hidden" name="javaSource" value="${notes.fileName}">
        <input type="hidden" name="version" value="${notes.versionId}">
        <input type="hidden" name="task" value="2">
        <input type="submit" value="Submit">
    </form>
    <hr/>
    <center><h2>The file:</h2></center>
    <div class="scrolling">
      <pre><font size="+1"><b>
          ${notes.thisVersion}
      </b></font></pre>
    </div>
    <hr/>
    <center><h2>End Session</h2></center>
    <form>
        <input type="hidden" name="sessionString" value="${requestScope.sessionString}">
        <input type="hidden" name="task" value="end"/>
        <input type="submit" value="Close Session"/>
    </form>
</font>
</body>
</html>