package control;

import blockchain.chain.*;
import com.google.gson.Gson;

import java.util.*;

public class Controller {

    private SimpleBlockchain<Transaction> chain;
    private Map<String, String> users;
    private Map<String, File> files;
    private Miner<Transaction> miner;
    private List<Transaction> transactionPool;

    /* Constructor */
    public Controller() {
        this.transactionPool = new ArrayList<>();
        this.chain = new SimpleBlockchain<>();
        this.miner = new Miner<>(chain);
        this.users = new HashMap<>();
        this.files = new HashMap<>();
    }

    public void isbv() {
        System.out.println(String.format("Chain is Valid: %s", chain.validate()));
    }

    private void mine(Transaction tx) {
        transactionPool.add(tx);
        if (transactionPool.size() > SimpleBlockchain.BLOCK_SIZE) {
            for(Transaction t: transactionPool) miner.mine(t);
            for(Transaction t: transactionPool) chain.add(t);
            transactionPool = new ArrayList<>();
        }
    }

    private void addUser(User user) {
        Transaction newUser = new Transaction(User.toTransaction(user));
        String hash = newUser.hash();
        users.put(user.getUserID(), hash);
        mine(newUser);
    }

    public void updateUser(User user) {
        addUser(user);
    }

    public String createUser(int userType, String userName, String password, String firstName, String lastName) {
        User newUser = new User(userType, userName, password, firstName, lastName);
        addUser(newUser);
        return newUser.getUserID();
    }

    private User getUser(String hash) {
        Gson gson = new Gson();

        for (Object o : chain.getChain()) {
            if (o == null) break;
            Block b = (Block) o;
            //if (!b.getHash().equals("root") && b.getTransactions().isEmpty()) System.out.println("null block");
            if (b.map.containsKey(hash))
                return gson.fromJson(Transaction.class.cast(b.map.get(hash)).getValue(), User.class);
        }

        for (Object o : transactionPool) {
            if (o == null) break;
            Transaction t = (Transaction) o;
            if (t.hash().equals(hash))
                return gson.fromJson(t.getValue(), User.class);
        }

        return null;
    }

    public User loginUser(String hash, String password) {
        User user = getUser(hash);
        if (user != null && user.checkPassword(password))
            return user;
        return null;
    }

    public String getUserHash(String userID) {
        return users.getOrDefault(userID, "none");
    }

    private String getPublicKey(String userID) {
        User user = getUser(getUserHash(userID));
        return user == null ? null : user.getPublicKey();
    }

    public void newFile(String fileContent, String targetUserID, User hospital) {
        if (hospital.getUserType() != 2) return;
        File file = hospital.createNewFile(fileContent, getPublicKey(targetUserID));
        User tempUser = getUser(getUserHash(targetUserID));
        assert tempUser != null;

        file = tempUser.acceptFile(file);                   // re-encrypt the file
        tempUser.addFile(new Transaction(file.fileName));   // add file to user's library
        files.put(file.fileName, file);                     // add file to the controller library
        updateUser(tempUser);                               // update chain with the new file
    }

    private String readFileContent(String fileID, String userID) {
        User user = getUser(getUserHash(userID));
        File file = files.get(fileID);

        assert user != null;
        if (user.getUserType() == 0) return user.readFile(file);
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
}
