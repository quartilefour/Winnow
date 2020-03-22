import React, {useState} from 'react';
import {Button, Card, Error, Form, Input, Logo} from "./HTMLElements";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faUser } from "@fortawesome/free-solid-svg-icons";

function Profile(props) {
    let state = props.location.state;

    const [error, setError] = useState(null);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    console.info(`Profile props: ${state.userEmail}`);

    return (
        <Card>
            <FontAwesomeIcon icon={faUser} color="cornflowerblue" size="6x"/>
            <Form>
                <Error>{error}</Error>
                <Input
                    type="text"
                    name="firstName"
                    value={firstName}
                    onChange={e => {
                        setFirstName(e.target.value);
                    }}
                    placeholder="First Name"
                />
                <Input
                    type="text"
                    name="lastName"
                    value={lastName}
                    onChange={e => {
                        setLastName(e.target.value);
                    }}
                    placeholder="Last Name"
                />
                <Input
                    type="email"
                    name="userEmail"
                    value={userEmail}
                    onChange={e => {
                        setUserEmail(e.target.value);
                    }}
                    placeholder="E-mail Address"
                />
                <Input
                    type="password"
                    name="userPassword"
                    value={userPassword}
                    onChange={e => {
                        setUserPassword(e.target.value);
                    }}
                    placeholder="Password"
                />
                <Input
                    type="password"
                    name="passwordConfirm"
                    value={passwordConfirm}
                    onChange={e => {
                        setPasswordConfirm(e.target.value);
                    }}
                    placeholder="Confirm Password"
                />
                <Button onClick={null}>Update</Button>
            </Form>
        </Card>
    )
}

export default Profile;