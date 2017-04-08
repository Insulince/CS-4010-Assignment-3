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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class SessionServlet extends HttpServlet {
    private static final int IP_INDEX = 0;
    private static final int DATE_INDEX = 1;
    private static final int NAME_INDEX = 2;

    private static String noName = "no username given";

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
                    "           <h1>assignment_3.SessionServlet Deployed</h1>" +
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
        String ip = req.getRemoteAddr();
        for (String[] session : this.sessions) {
            if (session[0].equals(ip)) {
                isFirstVisit = false;
                currentSession = session;
                break;
            }
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

            String[] new_session = {ip, this.dateFormat.format(new Date()), "need a username"};
            this.sessions.add(new_session);
            forwardTo.accept("startSession.jsp");
            return;
        }

        //If this is not the user's first visit to the site...

        String username = "";
        String password;
        if (currentSession[NAME_INDEX].equals(noName)) { //If no name was set for this session...
            username = req.getParameter("username");
            password = req.getParameter("password");
            if (username == null || username.trim().length() == 0 || checkPassword(username, password)) { //If the credentials are invalid...
                this.sessions.remove(currentSession);
                req.setAttribute("sessionCount", this.sessions.size());
                forwardTo.accept("startSession.jsp");
                return;
            }
        }

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