package control;


import blockchain.chain.Transaction;
import com.google.gson.Gson;
import cryption.*;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static cryption.RSA.encrypt;

public class User {
    private String userID;      // Unique user identifier
    private String userType;    // patient, doctor, hospital, pharmacy, insurance
    private String fullName;    // full name
    private String userName;    // login name
    private String password;    // login password
    private String firstName;   // user credentials: name
    private String lastName;    // user credentials: surname
    private String birth;       // user credentials: date of birth
    private String salt;        // AES salt
    private String publicKey;   // RSA public key
    private String privateKey;  // RSA private key

    private List<String> files; // list of fileID's
    private Map<String, List<String>> filePermissions;

    // Constructor
    public User(String userType, String userName, String password, String firstName, String lastName) {
        this.userType = userType;
        this.userID = UUID.randomUUID().toString();
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;

        this.files = new ArrayList<>();
        this.filePermissions = new HashMap<>();

        generateRSAKeys();
        this.salt = publicKey;
    }

    // Generates RSA keys for new users
    private void generateRSAKeys() {
        RSAKeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = new RSAKeyPairGenerator();
            this.publicKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
            this.privateKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Transforms a java object (User) to a string
    static String toTransaction(User user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }

    // Hospital: Encrypts a new file using target user's public key
    FileRecord createNewFile(String data, String publicKey, String type, String fileName) {
        try {
            String encryptedString = Base64.getEncoder().encodeToString(encrypt(data, publicKey));
            return new FileRecord(encryptedString, type, fileName);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /*
        Patient: Takes previously encrypted file (by user's public key)
        and encrypts it again by using user's own AES key.
    */
    FileRecord acceptFile(FileRecord file) {
        try {
            String decryptedString = RSA.decrypt(file.data, privateKey);
            String encryptedString = AES.encrypt(decryptedString, privateKey, salt);
            return new FileRecord(encryptedString, file.type, file.fileName, file.date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Patient: decrypt file
    String readFile(FileRecord file) {
        return AES.decrypt(file.data, privateKey, salt);
    }

    // Patient: Registers added file name
    void addFile(Transaction fileTransaction) {
        files.add(fileTransaction.getValue());
    }

    // Patient
    public void removeFile(Transaction file) {
        files.remove(file.getValue());
    }

    /*
        Grants file access permissions as:
        For patient: <fileID, List<Doctors>>
        For doctor : <patientID, List<Files>>
     */
    void grantFilePermission(String fileID, String userID) {
        // store permission in a new list
        List<String> permissions = new ArrayList<>();
        permissions.add(userType.equals("patient") ? userID : fileID);

        // add previous permissions to new list and update user's permissions with this list
        if (filePermissions.containsKey(userType.equals("patient") ? fileID : userID))
            permissions.addAll(filePermissions.get(userType.equals("patient") ? fileID : userID));
        filePermissions.put(userType.equals("patient") ? fileID : userID, permissions);
    }

    // Patient: Checks if the given doctor has permission to access given file
    boolean checkFilePermission(String userID, String fileName) {
        return filePermissions.containsKey(fileName) && filePermissions.get(fileName).contains(userID);
    }

    boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserID() {
        return userID;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public List<String> getFiles() {
        return files;
    }

    public Map<String, List<String>> getFilePermissions() {
        return filePermissions;
    }
}
