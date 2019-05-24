import http from "./httpService";
import { apiUrl } from "../config.json";

const apiEndpoint = apiUrl;

function fileUrl(id) {
  return `${apiEndpoint + "files"}/${id}`;
}

function userFileUrl(id) {
  return `${apiEndpoint + "/user_files"}/${id}`;
}

export function getFiles() {
  return http.get(apiEndpoint + "/files");
}

export function getFile(fileID) {
  return http.get(fileUrl(fileID));
}

export function getUserFiles(uid) {
  return http.get(userFileUrl(uid));
}

export function saveFile(file) {
  if (file.fileID) {
    const body = [...file];
    delete body.fileID;
    return http.put(fileUrl(file.fileID), body);
  }
  return http.post(apiEndpoint, file);
}

export function deleteFile(fileID) {
  http.delete(fileUrl(fileID));
}
