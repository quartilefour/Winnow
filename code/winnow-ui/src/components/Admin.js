import React, {useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import {Error} from "./HTMLElements";

/**
 * Renders the Dashboard landing page for authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Admin(props) {

    const [error, setError] = useState('');

    return (
        <div className="tab-container">
            <Error>{error}</Error>
            <Tab.Container id="left-tabs-example" defaultActiveKey="teams" className="tab-container">
                <Row>
                    <Col sm={3}>
                        <Nav variant="pills" className="flex-column">
                            <Nav.Item>
                                <Nav.Link eventKey="roles">Roles</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="teams">Teams</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="meshcat">MeSH Categories</Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Col>
                    <Col sm={9}>
                        <Tab.Content>
                            <Tab.Pane eventKey="roles">
                                <p>User Role Management</p>
                            </Tab.Pane>
                            <Tab.Pane eventKey="teams">
                                <p>Team Management</p>
                            </Tab.Pane>
                            <Tab.Pane eventKey="meshcat">
                                <p>MesH Category Management</p>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Admin;