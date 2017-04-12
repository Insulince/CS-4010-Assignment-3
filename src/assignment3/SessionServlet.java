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
    private static final int IDENTIFIER_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final String NO_NAME = "no username given";

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

        System.out.println(usersString);
        if (usersString != null && !Objects.equals(usersString, "")) {
            System.out.println("usersString != null; does = " + usersString);

            int counter = 0;
            for (String[] session : this.sessions) {
                if (session[IDENTIFIER_INDEX].equals(usersString)) {
                    System.out.println("Session match!\n---" + session[IDENTIFIER_INDEX] + "\n---" + session[DATE_INDEX] + "\n---" + session[NAME_INDEX]);
                    isFirstVisit = false;
                    currentSession = session;
                    break;
                }

                counter++;

                if (counter == this.sessions.size()) {
                    System.out.println("No match for session.");
                    forwardTo.accept("startSession.jsp");
                    return;
                }
            }
        }

        if (req.getParameter("task") == null) {
            System.out.println("Sending to login");

            forwardTo.accept("startSession.jsp");
            return;
        }

        if (req.getParameter("task") == null && !isFirstVisit) { //If there is no task...
            System.out.println("Task = null && !isFirstVisit");
            this.sessions.remove(currentSession);
            isFirstVisit = true;
        }
        req.setAttribute("sessionCount", this.sessions.size());
        if (isFirstVisit) { //If this is the user's first visit to the site...
            System.out.println("Is first visit");

            if (this.sessions.size() == 10) { //If the session limit is reached...
                System.out.println("Session size = 10");

                forwardTo.accept("noSessions.jsp");
                return;
            }

            //If the session limit has not been reached...
            System.out.println("Session size != 10");

            String sessionString = getRandomString();
            String[] newSession = {sessionString, this.dateFormat.format(new Date()), NO_NAME};
            this.sessions.add(newSession);
            req.setAttribute("sessionString", sessionString);
            req.setAttribute("sessionCount", this.sessions.size());
            forwardTo.accept("getNotes.jsp");
            return;
        }

        //If this is not the user's first visit to the site...

        if (req.getParameter("task").equals("end")) { //If the task is "end"...
            System.out.println("Ending session.");
            this.sessions.remove(currentSession);
            req.getSession().invalidate();
            req.setAttribute("sessionCount", this.sessions.size());
            forwardTo.accept("startSession.jsp");
            return;
        }

        //If the task is not "end"...

        String username = "";
        String password;

        //If the credentials are valid...

        currentSession[NAME_INDEX] = username.trim();
        req.setAttribute("username", currentSession[NAME_INDEX]);
        if (sessionExpired(currentSession[DATE_INDEX], this.dateFormat.format(new Date()))) { //If the session has expired...
            this.sessions.remove(currentSession);
            forwardTo.accept("Expired.jsp");
            return;
        }

        //If the session has not expired...

        currentSession[DATE_INDEX] = this.dateFormat.format(new Date());
        NotesBean notes = new NotesBean();
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
        return false;
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