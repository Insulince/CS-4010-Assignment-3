package assignment3.byteshex;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class NotesBean implements Serializable {
    private static final int FILENAME_INDEX = 0;
    private static final int VERSION_ID_INDEX = 0;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/cs4010";
    static final String USER = "cs4010";
    static final String PASS = "cs4010";
    private int javaStoreId = -1;
    private String fileName = "";
    private int versionId = 0;
    private String saveTime = "";
    private String thisVersion = "";
    private String notes = "";

    public NotesBean() {
    }

    public void setAll(String parameterString) {
        String[] parameters = parameterString.split(",");
        fileName = parameters[FILENAME_INDEX].trim();
        versionId = Integer.parseInt(parameters[VERSION_ID_INDEX].trim());
        getAll();
    }

    public void setAll(String fileName, int version) {
        try {
            System.out.println(fileName);
            System.out.println(version);
            this.versionId = -2;
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            String query = " SELECT * from java_store WHERE  file_name='" + fileName + "' AND version_id=" + version + ";";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                this.javaStoreId = resultSet.getInt("java_store_id");
                this.fileName = resultSet.getString("file_name");
                this.versionId= resultSet.getInt("version_id");
                this.saveTime = resultSet.getString("save_time");
                this.thisVersion = BytesHex.hexStringToString(resultSet.getString("this_version"));
                this.notes = BytesHex.hexStringToString(resultSet.getString("notes"));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
        }
        return;
    }

    public static String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getUSER() {
        return USER;
    }

    public static String getPASS() {
        return PASS;
    }

    public int getJavaStoreId() {
        return javaStoreId;
    }

    public void setJavaStoreId(int javaStoreId) {
        this.javaStoreId = javaStoreId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getThisVersion() {
        return thisVersion;
    }

    public void setThisVersion(String thisVersion) {
        this.thisVersion = thisVersion;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private void getAll() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            String query = " SELECT * FROM java_store WHERE  file_name='" + this.fileName + "' AND version_id=" + this.versionId + ";";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                this.javaStoreId = resultSet.getInt("java_store_id");
                this.fileName = resultSet.getString("file_name");
                this.versionId = resultSet.getInt("version_id");
                this.saveTime = resultSet.getString("save_time");
                this.thisVersion = BytesHex.hexStringToString(resultSet.getString("this_version"));
                this.notes = BytesHex.hexStringToString(resultSet.getString("notes"));
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNotes(String notes, String fileName, int versionId) {
        this.notes = notes;
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();
            String query = "UPDATE java_store SET notes='" + BytesHex.stringToHexString(notes) + "' WHERE  file_name='" + fileName + "' AND version_id=" + versionId + ";";
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
