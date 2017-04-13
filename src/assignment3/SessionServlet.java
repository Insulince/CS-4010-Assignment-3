package assignment3;

import assignment3.byteshex.NotesBean;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Consumer;

public class SessionServlet extends HttpServlet {
    private static final int SESSION_STRING_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int NAME_INDEX = 2;

    private List<String[]> sessions;
    private DateFormat dateFormat;

    public void init() throws ServletException {
        this.sessions = new ArrayList<>();
        this.dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (req.getParameter("task") != null && req.getParameter("task").trim().equals("deploy")) { //If task is "deploy"...
            PrintWriter out = res.getWriter();
            out.println("" +
                    "<html>" +
                    "   <head>" +
                    "   </head>" +
                    "   <body>" +
                    "       <hr/>" +
                    "       <center>" +
                    "           <h1>SessionServlet Deployed</h1>" +
                    "       </center>" +
                    "       <hr/>" +
                    "   </body>" +
                    "</html>");
            return;
        }

        //If the task is not "deploy"...

        Consumer<String> forwardTo = (string) -> forwardTo(string, req, res);
        boolean isFirstVisit = true;
        String[] currentSession = new String[3];
        String usersString = req.getParameter("sessionString");

        if (usersString != null && !Objects.equals(usersString, "")) { //If the user provided a sessionString...
            int counter = 0;
            if (!Objects.equals(usersString, "no-session-string-yet")) {
                for (String[] session : this.sessions) { //For each session...
                    if (session[SESSION_STRING_INDEX].equals(usersString)) { //If the current session's sessionString is the provided sessionString...
                        isFirstVisit = false; //This user has been here before.
                        currentSession = session; //This is the current session.
                        break;
                    }

                    counter++; //This session doesn't match, so increment.

                    if (counter == this.sessions.size()) { //If we checked every session without finding the matching one...
                        forwardTo.accept("startSession.jsp"); //Something went wrong and this session is not recognized as valid. Send to login screen.
                        return;
                    }
                }
            }
        }

        if (req.getParameter("task") == null) { //If no task was provided...
            forwardTo.accept("startSession.jsp"); //A task is required, so send user to log in.
            return;
        }

        if (req.getParameter("task") == null && !isFirstVisit) { //If there is no task...
            this.sessions.remove(currentSession);
            isFirstVisit = true;
        }

        req.setAttribute("sessionCount", this.sessions.size());
        if (isFirstVisit) { //If this is the user's first visit to the site...
            if (this.sessions.size() == 10) { //If the session limit is reached...

                forwardTo.accept("noSessions.jsp");
                return;
            }

            //If the session limit has not been reached...

            String username = req.getParameter("username");
            String password = req.getParameter("password");

            if (username != null && username.trim().length() != 0 && checkPassword(username, password)) { //If the credentials are invalid...
                String sessionString = getRandomString();
                String[] newSession = {sessionString, this.dateFormat.format(new Date()), username};
                this.sessions.add(newSession);
                req.setAttribute("sessionString", sessionString);
                req.setAttribute("sessionCount", this.sessions.size());
                forwardTo.accept("getNotes.jsp");
                return;
            } else {
                //Invalid credentials.
                forwardTo.accept("sessionStart.jsp");
                return;
            }
        }

        //If this is not the user's first visit to the site...

        if (req.getParameter("task").equals("end")) { //If the task is "end"...
            this.sessions.remove(currentSession); //Remove this session.
            req.getSession().invalidate(); //Invalidate it.
            req.setAttribute("sessionCount", this.sessions.size());
            forwardTo.accept("startSession.jsp"); //Send to login.
            return;
        }

        //If the task is not "end"...

        req.setAttribute("username", currentSession[NAME_INDEX]);
        if (sessionExpired(currentSession[DATE_INDEX], this.dateFormat.format(new Date()))) { //If the session has expired...
            this.sessions.remove(currentSession);
            forwardTo.accept("Expired.jsp");
            return;
        }

        //If the session has not expired...

        currentSession[DATE_INDEX] = this.dateFormat.format(new Date());

        serialBeanInstantiation(req, forwardTo);
    }

    private synchronized void serialBeanInstantiation(HttpServletRequest req, Consumer forwardTo) { //Serial access to the database GUARANTEED by the "synchronized" keyword.
        NotesBean notes = new NotesBean(); //Bean instantiation handled in the Servlet and passed to the JSP.

        if (!req.getParameter("task").trim().equals("0")) { //If task does not equal 0 (getting notes)...
            notes.setAll(req.getParameter("javaSource"), Integer.parseInt(req.getParameter("version")));
            if (req.getParameter("task").trim().equals("2")) { //If task is 2 (editing notes)...
                notes.setNotes(req.getParameter("notes"), req.getParameter("javaSource"), Integer.parseInt(req.getParameter("version")));
            }
        }
        req.setAttribute("sessionCount", this.sessions.size());
        req.setAttribute("notes", notes);
        req.setAttribute("sessionString", req.getParameter("sessionString"));

        forwardTo.accept("getNotes.jsp");
    }

    private boolean sessionExpired(String sessionDate, String now) {
        return false;
    }

    private boolean checkPassword(String username, String password) {
        return true;
    }

    public void log(String message) {
        FileWriter fileWriter = null;
        try {
            String content = "Message: \"" + message + "\" at :" + new Date(System.currentTimeMillis()).toString() + ".\n";
            File logFile = new File("C:/Tomcat/webapps/js_test/session.log");
            fileWriter = new FileWriter(logFile, true);
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void forwardTo(String url, HttpServletRequest req, HttpServletResponse res) {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(url);
        try {
            requestDispatcher.forward(req, res);
        } catch (IOException | ServletException exception) {
            log("req from " + url + " not forwarded at ");
            try {
                throw exception;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
        log("The instance was destroyed");
    }

    public String getRandomString() {
        byte[] randomByteArray = new byte[10];
        Random randomNumberGenerator = new Random(System.currentTimeMillis());

        for (int index = 0; index < 10; ++index) {
            int randomInt = randomNumberGenerator.nextInt(26);
            randomByteArray[index] = (byte) (randomInt + 65);
        }

        try {
            return new String(randomByteArray, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "Random String generation failed.";
        }
    }
}