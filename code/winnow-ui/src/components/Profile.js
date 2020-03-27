import React, {useState, useEffect} from 'react';
import {Form, Col, Button} from 'react-bootstrap';
import {Card, Error} from "./HTMLElements";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faUser} from "@fortawesome/free-solid-svg-icons";
import {fetchProfileData} from "../service/AuthService";

function Profile(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [error, setError] = useState(null);
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [userEmail, setUserEmail] = useState("");
    const [userPassword, setUserPassword] = useState("");
    const [passwordConfirm, setPasswordConfirm] = useState("");

    useEffect(() => {
        fetchProfileData()
            .then(res => {
                setFirstName(res.firstName);
                setLastName(res.lastName);
                setUserEmail(res.userEmail);
                setIsLoaded(true);
            }).catch(err => {
            setError(err);
            setIsLoaded(true);
        });
    }, []);

    //console.info(`Profile props: ${JSON.stringify(user)}`);
    //console.info(`Profile data: ${JSON.stringify(data)}`);
    if (isLoaded) {
        return (
            <Card id="cardUserProfile">
                <FontAwesomeIcon id="profileImg" icon={faUser} color="cornflowerblue" size="6x"/>
                <Form>
                    <Form.Group
                        className="fgProfile"
                    >
                        <Error>{error}</Error>
                        <Form.Row>
                            <Col>
                                <Form.Control
                                    type="text"
                                    name="firstName"
                                    value={firstName}
                                    onChange={e => {
                                        setFirstName(e.target.value);
                                    }}
                                    placeholder="First Name"
                                />
                            </Col>
                            <Col>
                                <Form.Control
                                    type="text"
                                    name="lastName"
                                    value={lastName}
                                    onChange={e => {
                                        setLastName(e.target.value);
                                    }}
                                    placeholder="Last Name"
                                />
                            </Col>
                        </Form.Row>
                        <Form.Row>
                            <Col>
                                <Form.Control
                                    type="email"
                                    name="userEmail"
                                    value={userEmail ? userEmail : undefined}
                                    onChange={e => {
                                        setUserEmail(e.target.value);
                                    }}
                                    placeholder="E-mail Address"
                                />
                            </Col>
                        </Form.Row>
                        <Button onClick={null} disabled={true}>Update</Button>
                    </Form.Group>
                    <Form.Group
                        className="fgProfile"
                        label="Change Password"
                    >
                        <Form.Row>
                            <Col>
                                <Form.Control
                                    type="password"
                                    name="userPassword"
                                    value={userPassword}
                                    onChange={e => {
                                        setUserPassword(e.target.value);
                                    }}
                                    placeholder="Password"
                                />
                            </Col>
                        </Form.Row>
                        <Form.Row>
                            <Col>
                                <Form.Control
                                    type="password"
                                    name="passwordConfirm"
                                    value={passwordConfirm}
                                    onChange={e => {
                                        setPasswordConfirm(e.target.value);
                                    }}
                                    placeholder="Confirm Password"
                                />
                            </Col>
                        </Form.Row>
                        <Button onClick={null} disabled={true}>Change</Button>
                    </Form.Group>
                </Form>
            </Card>
        )
    } else {
        return (
            <div>Loading...</div>
        )
    }
}

export default Profile;