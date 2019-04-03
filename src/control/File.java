package control;

import java.util.UUID;

public class File {
    public String fileName;
    public String data;

    public File(String data) {
        this.data = data;
        this.fileName = UUID.randomUUID().toString().replace("-", "");
    }
}
