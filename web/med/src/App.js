import React, { Component } from 'react';
import {Route, Redirect, Switch} from 'react-router-dom';
import {ToastContainer} from 'react-toastify';
import Home from './components/home';
import NotFound from './components/notFound';
import NavBar from './components/navBar';
import LoginForm from './components/loginForm';
import Logout from './components/logout';
import RegisterForm from './components/registerForm';
import auth from './services/authService';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

class App extends Component {
  state = {};

  componentDidMount() {
    const user = auth.getCurrentUser();
    this.setState({user});
    document.title = 'Health Miner'
  }

  render() {
    const {user} = this.state;

    return (
        <React.Fragment>
          <ToastContainer/>
          <NavBar user={user}/>
          <main className="container">
            <Switch>
              <Route path="/register" component={RegisterForm}/>
              <Route path="/login" component={LoginForm}/>
              <Route path="/logout" component={Logout}/>
              <Route
                  path="/home"
                  render={props => <Home {...props} user={user}/>}
              />
              <Route path="/not-found" component={NotFound}/>
              <Redirect from="/" exact to="/home"/>
              <Redirect to="/not-found"/>
            </Switch>
          </main>
        </React.Fragment>
    );
  }
}

export default App;
