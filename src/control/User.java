package control;


import blockchain.chain.Transaction;
import com.google.gson.Gson;
import control.File;
import cryption.*;

import java.security.NoSuchAlgorithmException;
import java.util.*;

import static cryption.RSA.encrypt;

public class User {
    private int userType;       // 0: Patient, 1: Doctor, 2: Hospital
    private String userID;      // Unique user identifier
    private String userName;    // login name
    private String password;    // login password
    private String firstName;   // user credentials
    private String lastName;
    private String key;         // AES encryption key
    private String salt;        // AES salt
    private String publicKey;   // RSA public key
    private String privateKey;  // RSA private key

    private List<String> files; // list of file names
    private Map<String, List<String>> filePermissions;

    // Constructor
    public User(int userType, String userName, String password, String firstName, String lastName) {
        this.userType = userType;
        this.userID = UUID.randomUUID().toString();
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;

        this.files = new ArrayList<>();
        this.filePermissions = new HashMap<>();

        generateRSAKeys();
        generateNewKey();
    }

    // Generates AES key for new users
    private void generateNewKey() {
        //SecureRandom secureRandom = new SecureRandom();
        //byte[] token = new byte[16];
        //secureRandom.nextBytes(token);
        //this.key = new BigInteger(1, token).toString(16);
        this.key = privateKey;
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
    File createNewFile(String data, String publicKey) {
        try {
            String encryptedString = Base64.getEncoder().encodeToString(encrypt(data, publicKey));
            return new File(encryptedString);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /*
        Patient: Takes previously encrypted file (by user's public key)
        and encrypts it again by using user's own AES key.
    */
    File acceptFile(File file) {
        try {
            String decryptedString = RSA.decrypt(file.data, privateKey);
            String encryptedString = AES.encrypt(decryptedString, key, salt);
            return new File(encryptedString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Patient: decrypt file
    String readFile(File file) {
        return AES.decrypt(file.data, key, salt);
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
        For patient: <fileName, List<Doctors>>
        For doctor : <patientID, List<Files>>
     */
    void grantFilePermission(String fileName, String userID) {
        // store permission in a new list
        List<String> permissions = new ArrayList<>();
        permissions.add(userType == 0 ? userID : fileName);

        // add previous permissions to new list and update user's permissions with the list
        if (filePermissions.containsKey(userType == 0 ? fileName : userID))
            permissions.addAll(filePermissions.get(userType == 0 ? fileName : userID));
        filePermissions.put(userType == 0 ? fileName : userID, permissions);
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
        return firstName + lastName;
    }

    public int getUserType() {
        return userType;
    }

    public String getUserID() {
        return userID;
    }

    String getPublicKey() {
        return publicKey;
    }

    public List<String> getFiles() {
        return files;
    }

    public Map<String, List<String>> getFilePermissions() {
        return filePermissions;
    }
}
