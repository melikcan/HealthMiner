package control;

import blockchain.chain.*;
import com.google.gson.Gson;

import java.util.*;

public class Manager {

    private SimpleBlockchain<Transaction> chain;    // main block chain
    private Map<String, String> users;              // userID book
    private Map<String, FileRecord> files;          // file database
    private Miner<Transaction> miner;               // miner object
    private List<Transaction> transactionPool;      // waiting room for the transactions to be mined

    /* Constructor */
    public Manager() {
        this.transactionPool = new ArrayList<>();
        this.chain = new SimpleBlockchain<>();
        this.miner = new Miner<>(chain);
        this.users = new HashMap<>();
        this.files = new HashMap<>();
    }

    // mine transactions if the maximum block size is reached
    private void mine(Transaction tx) {
        transactionPool.add(tx);
        if (transactionPool.size() > SimpleBlockchain.BLOCK_SIZE) {
            System.out.println("Mining a new block...");
            for (Transaction t : transactionPool) miner.mine(t);
            for (Transaction t : transactionPool) chain.add(t);
            transactionPool = new ArrayList<>();
            System.out.println("Mining done!");
        }
    }

    // create a new user
    public String createUser(String userType, String userName, String password, String firstName, String lastName) {
        User newUser = new User(userType, userName, password, firstName, lastName);
        addUser(newUser);
        return newUser.getUserID();
    }

    // createUser helper method
    private void addUser(User user) {
        Transaction newUser = new Transaction(User.toTransaction(user));
        users.put(user.getUserID(), newUser.hash());
        mine(newUser);
    }

    // user update
    public void updateUser(User user) {
        addUser(user);
    }

    // reach user object for the given hash
    private User getUser(String hash) {
        Gson gson = new Gson();

        for (Object o : chain.getChain()) {
            if (o == null) break;
            Block b = (Block) o;
            if (b.map.containsKey(hash))
                return gson.fromJson(((Transaction) b.map.get(hash)).getValue(), User.class);
        }

        for (Object o : transactionPool) {
            if (o == null) break;
            Transaction t = (Transaction) o;
            if (t.hash().equals(hash))
                return gson.fromJson(t.getValue(), User.class);
        }

        return null;
    }

    // check user login credentials
    public User loginUser(String hash, String password) {
        User user = getUser(hash);
        if (user != null && user.checkPassword(password))
            return user;
        System.out.println("Wrong username or password!");
        return null;
    }

    // login with token
    public User loginUser2(String hash) {
        return getUser(hash);
    }

    public String getUserHash(String userID) {
        return users.getOrDefault(userID, "none");
    }

    // get user's public RSA key
    private String getPublicKey(String userID) {
        User user = getUser(getUserHash(userID));
        return user == null ? null : user.getPublicKey();
    }

    // register a new file in the system
    public void newFile(String fileContent, String targetUserID, User writer, String fileName) {
        String type = matchFileType(writer.getUserType());  // decide on file type
        String key = getPublicKey(targetUserID);            // get encryption key

        FileRecord file = writer.createNewFile(fileContent, key, type, fileName);   // create file

        User tempUser = getUser(getUserHash(targetUserID));
        assert tempUser != null;

        file = tempUser.acceptFile(file);                   // re-encrypt the file
        tempUser.addFile(new Transaction(file.fileID));     // add file to user's library
        files.put(file.fileID, file);                       // add file to the controller library
        updateUser(tempUser);                               // update chain with the new file
    }

    // newFile helper method
    private static String matchFileType(String userType) {
        switch (userType) {
            case "doctor":
                return "Prescription";
            case "hospital":
                return "Test";
            case "insurance":
                return "Insurance";
            default:
                return "Report";
        }
    }

    // sends encrypted file content to user for decrypting
    private String readFileContent(String fileID, String userID) {
        User user = getUser(getUserHash(userID));
        FileRecord file = files.get(fileID);

        assert user != null;
        if (user.getUserType().equals("patient")) return user.readFile(file);
        return null;
    }

    // patient trying to reach its own file
    public String reachFileContent(String fileID, User patient) {
        return readFileContent(fileID, patient.getUserID());
    }

    // doctor trying to access a patient file
    public String reachFileContent(String fileID, String patientID, User doctor) {
        User patient = getUser(getUserHash(patientID));

        if (patient != null && patient.checkFilePermission(doctor.getUserID(), fileID))
            return readFileContent(fileID, patientID);
        return null;
    }

    // grant given file access to current doctor
    public User grantAccess(String fileID, String patientID, User doctor) {
        User patient = getUser(getUserHash(patientID));

        assert patient != null;
        patient.grantFilePermission(fileID, doctor.getUserID());
        updateUser(patient);

        doctor.grantFilePermission(fileID, patientID);
        updateUser(doctor);

        return doctor;
    }

    public Map<String, FileRecord> getFiles() {
        return files;
    }

    public FileRecord getFile(String fileID) {
        return files.get(fileID);
    }
}
