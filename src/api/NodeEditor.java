package api;

import control.FileRecord;
import control.Manager;
import control.SampleRecords;
import control.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeEditor {
    private Manager control;
    private Map<String, String> userBook;

    public NodeEditor() {

        control = new Manager();
        userBook = new HashMap<>();
        demoSetUsers();
        testUsers();
    }

    String addUser(String type, String userName, String password, String name, String surname) {
        String uid = control.createUser(type, userName, password, name, surname);
        userBook.put(userName, uid);
        return uid;
    }

    private void demoSetUsers() {
        addUser("patient", "user1", "123", "Jules", "Winnfield");
        addUser("patient", "user2", "123", "Everett", "Baldwin");
        addUser("patient", "user3", "123", "Adaline", "Downer");
        addUser("patient", "user4", "123", "Dirk", "Varley");
        addUser("patient", "user5", "123", "Baz", "Payton");
        addUser("patient", "user6", "123", "Egbert", "Lee");
        addUser("patient", "user7", "123", "Darby", "Hollands");
        addUser("patient", "user8", "123", "Goddard", "Warwick");
        addUser("patient", "user9", "999", "Malcom", "Peck");
        addUser("doctor", "doctor1", "123", "Kandace", "Faulkner");
        addUser("doctor", "doctor2", "123", "Betsy", "Walters");
        addUser("hospital", "hospital", "123", "Medical", "Center");
        addUser("insurance", "insurance", "123", "Insurance", "Company");
        addUser("pharmacy", "pharmacy", "123", "Daisy", "Pharmacy");
    }

    private void testUsers(){
        User currentUser = control.loginUser(control.getUserHash(userBook.get("hospital")), "123");

        String target1 = userBook.get("user1");
        String target2 = userBook.get("user2");
        String target3 = userBook.get("user3");
        String target4 = userBook.get("user4");
        String target5 = userBook.get("user5");
        String target6 = userBook.get("user6");
        String target7 = userBook.get("user7");
        String target8 = userBook.get("user8");
        String target9 = userBook.get("user9");

        control.newFile(SampleRecords.file1, target1, currentUser, "Blood Result 1");
        control.newFile(SampleRecords.file1, target1, currentUser, "Test Results");
        control.newFile(SampleRecords.file1, target2, currentUser, "Blood Result 1");
        control.newFile(SampleRecords.file2, target2, currentUser, "Blood Result 2");
        control.newFile(SampleRecords.file3, target3, currentUser, "Blood Result 3");
        control.newFile(SampleRecords.file4, target4, currentUser, "Blood Result 4");
        control.newFile(SampleRecords.file5, target5, currentUser, "Blood Result 5");
        control.newFile(SampleRecords.file6, target6, currentUser, "Blood Result 6");
        control.newFile(SampleRecords.file7, target7, currentUser, "Blood Result 7");
        control.newFile(SampleRecords.file8, target8, currentUser, "Blood Result 8");
        control.newFile(SampleRecords.file9, target9, currentUser, "Blood Result 9");

        currentUser = control.loginUser(control.getUserHash(userBook.get("user1")), "123");
        control.newFile(SampleRecords.file8, target1, currentUser, "Week 1 - Report");
        control.newFile(SampleRecords.file9, target1, currentUser, "Week 2 - Report");
        control.newFile(SampleRecords.file6, target1, currentUser, "Week 3 - Report");
        control.newFile(SampleRecords.file6, target1, currentUser, "Week 4 - Report");
        control.newFile(SampleRecords.file6, target1, currentUser, "Week 5 - Report");
        String file1 = currentUser.getFiles().get(0);

        currentUser = control.loginUser(control.getUserHash(userBook.get("insurance")), "123");
        control.newFile(SampleRecords.file4, target1, currentUser, "Health Insurance");

        currentUser = control.loginUser(control.getUserHash(userBook.get("doctor1")), "123");
        control.newFile(SampleRecords.file7, target1, currentUser, "Vitamin");
        control.newFile(SampleRecords.file7, target1, currentUser, "Painkillers");
        currentUser = control.grantAccess(file1, userBook.get("user1"), currentUser);
        control.updateUser(currentUser);
    }

    User getUser(String userID) {
        String userHash = control.getUserHash(userID);
        return control.loginUser2(userHash);
    }

    Map<String, FileRecord> getFiles() {
        return control.getFiles();
    }

    FileRecord getFile(String fileID){
        return control.getFile(fileID);
    }

    List<FileRecord> getUserFiles(String userID) {
        User user = getUser(userID);
        List<FileRecord> files = new ArrayList<>();
        for(String f: user.getFiles()) {
            files.add(getFile(f));
        }
        return files;
    }

    User loginUser(LoginModel login){
        String userID = userBook.get(login.username);
        String userHash = control.getUserHash(userID);
        return control.loginUser(userHash, login.password);
    }
}
