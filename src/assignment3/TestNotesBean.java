package assignment3;

import assignment3.byteshex.NotesBean;

public class TestNotesBean {
    public static void main(String[] args) {
        NotesBean notes = new NotesBean();
        notes.setAll("littlefile.java", 1);
        System.out.println(notes.getThisVersion());
        System.out.println(notes.getNotes());
        System.out.println("--------------");
        notes.setNotes("test2 little file", "littlefile.java", 1);
        System.out.println(notes.getNotes());
        System.out.println("--------------");
        notes.setAll("little.java", 1);
        System.out.println(notes.getVersionId());
    }
}
