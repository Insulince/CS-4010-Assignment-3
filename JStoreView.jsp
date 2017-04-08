<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="notesBean" class="assignment3.beans.NotesBean" scope="page"></jsp:useBean>
<c:set var="note">${param.javaval}</c:set>
<jsp:setProperty name="notesBean" property="all" value="${note}"/>

<html>
<head>
    <title>Test NoteBean</title>
</head>
<body>
<h1>
    <center> Test NoteBean.java</center>
</h1>
<hr/>
<h3>The Code:</h3>
<font size="+2" color="BLUE">
<pre><b>
    ${notesBean.thisVersion}
</b></pre>
</font>
<h3>The Notes:</h3>
<font size="+2" color="BLUE">
<pre><b>
    ${notesBean.notes}
</b></pre>
</font>
</body>
</html>



