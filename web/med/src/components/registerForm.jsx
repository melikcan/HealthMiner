import React from 'react';
import Joi from 'joi-browser';
import Form from './common/form';
import * as userService from '../services/userService';
import auth from '../services/authService';


class RegisterForm extends Form {
  state = {
    data: {userType: 'patient', username: '', password: '', name: '', surname: ''},
    errors: {},
    userTypes: [
        {"_id":"patient","name":"Patient"},
      {"_id":"doctor","name":"Doctor"},
      {"_id":"hospital","name":"Hospital"},
      {"_id":"insurance","name":"Insurance"},
      {"_id":"pharmacy","name":"Pharmacy"}
      ]
  };

  schema = {
    userType: Joi.string()
        .required()
        .label('User type'),
    username: Joi.string()
        .required()
        .label('Username'),
    password: Joi.string()
        .required()
        .min(5)
        .label('Password'),
    name: Joi.string()
        .required()
        .label('Name'),
    surname: Joi.string()
        .required()
        .label('Surname'),
  };

  doSubmit = async () => {
    try {
      const response = await userService.register(this.state.data);
      auth.loginWithJwt(response.data);
      window.location = '/';
    } catch (ex) {
      if (ex.response && ex.response.status === 400) {
        const errors = {...this.state.errors};
        errors.username = ex.response.data;
        this.setState({errors});
      }
    }
  };

  render() {
    return (
        <div>
          <h1>Register</h1>
          <form onSubmit={this.handleSubmit}>
            {this.renderInput('username', 'Username')}
            {this.renderInput('password', 'Password', 'password')}
            {this.renderInput('name', 'First Name')}
            {this.renderInput('surname', 'Last Name')}
            {this.renderSelect('userType', 'User type', this.state.userTypes)}
            {this.renderButton('Register')}
          </form>
        </div>
    );
  }
}

export default RegisterForm;
