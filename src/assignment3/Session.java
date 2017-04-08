package assignment3;

public class Session {
    public String ip;
    public String lastAccess;
    public String username;
    public String password;
    public String securityString;
    public int task;

    public Session(int initialTask) {
        this.task = initialTask;
    }

    public Session(String ip, String lastAccess, String username, String password, String securityString, int task) {
        this.ip = ip;
        this.lastAccess = lastAccess;
        this.username = username;
        this.password = password;
        this.securityString = securityString;
        this.task = task;
    }
}
