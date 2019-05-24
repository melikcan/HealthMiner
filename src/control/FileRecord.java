package control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class FileRecord {
    public String fileID;
    public String data;
    public String type; // available types: 'Test', 'Report', 'Prescription', 'Insurance'
    public String fileName;
    public String date;

    public FileRecord(String data, String type, String fileName) {
        this.fileID = UUID.randomUUID().toString().replace("-", "");
        this.data = data;
        this.type = type;
        this.fileName = fileName;
        this.date = setDate();
    }

    public FileRecord(String data, String type, String fileName, String date) {
        this.fileID = UUID.randomUUID().toString().replace("-", "");
        this.data = data;
        this.type = type;
        this.fileName = fileName;
        this.date = date;
    }

    private String setDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public String getFileID() {
        return fileID;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDate() {
        return date;
    }
}
