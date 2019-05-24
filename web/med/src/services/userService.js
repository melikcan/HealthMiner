import http from './httpService';
import {apiUrl} from '../config.json';

const apiEndpoint = apiUrl + '/users';

function userUrl(id) {
  return `${apiEndpoint}/${id}`;
}

export function register(user) {
  return http.post(apiEndpoint, {
    type: user.userType,
    userName: user.username,
    password: user.password,
    name: user.name,
    surname: user.surname,
  });
}

export function getUser(userID) {
  return http.get(userUrl(userID));
}