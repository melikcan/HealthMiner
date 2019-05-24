package api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import control.FileRecord;
import control.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AppController {

    private NodeEditor node = new NodeEditor();

    @CrossOrigin
    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUserById(@PathVariable String id) {
        return node.getUser(id);
    }

    @CrossOrigin
    @PostMapping("/users")
    public String registerNewUser(@RequestBody UserModel newUser) {
        String uid = node.addUser(newUser.type, newUser.userName, newUser.password, newUser.name, newUser.surname);
        return generateToken(uid, newUser.name + ' ' + newUser.surname, newUser.userName);
    }

    @CrossOrigin
    @GetMapping("/files")
    @ResponseBody
    public Map<String, FileRecord> getFileList() {
        return node.getFiles();
    }

    @CrossOrigin
    @GetMapping("/files/{id}")
    @ResponseBody
    public FileRecord getFileById(@PathVariable String id) {
        return node.getFile(id);
    }

    @CrossOrigin
    @GetMapping("/user_files/{id}")
    @ResponseBody
    public List<FileRecord> getUserFilesById(@PathVariable String id) {
        return node.getUserFiles(id);
    }

    @CrossOrigin
    @PostMapping("/auth")
    public String loginUser(@RequestBody LoginModel loginForm) {
        User logged = node.loginUser(loginForm);
        if (logged == null)
            return null; // invalid username or password
        return generateToken(logged.getUserID(), logged.getFullName(), logged.getUserName());
    }

    private String generateToken(String id, String name, String userName) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("name", name)
                .withClaim("username", userName)
                .sign(Algorithm.HMAC256("non-secure-secret-key"));
    }

}