import React, {useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";

/* istanbul ignore file */
/**
 * Renders the Admin dashboard landing page for admin users.
 *
 * @returns {*}
 * @constructor
 */
function Admin() {
    const [currentTab, setCurrentTab] = useState('teams');

    React.useEffect(() => {
        let storedTab = sessionStorage.getItem('adminTab');
        if (storedTab) {
            setCurrentTab(storedTab);
        }
    }, [currentTab]);

    return (
        <div className="main-tab-holder">
            <Tab.Container activeKey={currentTab} className="tab-container" onSelect={
                (e) => {
                    sessionStorage.setItem('adminTab', e);
                    setCurrentTab(e)
                }
            }>
                <Row>
                    <Col sm={3} className="outer-tabs">
                        <Nav variant="tabs" className="flex-column nav-tab-collection">
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
                    <Col sm={9} className="outer-tab-content">
                        <Tab.Content>
                            <Tab.Pane eventKey="roles">
                                <p className="tab-heading">User Role Management</p>
                            </Tab.Pane>
                            <Tab.Pane eventKey="teams">
                                <p className="tab-heading">Team Management</p>
                            </Tab.Pane>
                            <Tab.Pane eventKey="meshcat">
                                <p className="tab-heading">MesH Category Management</p>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Admin;