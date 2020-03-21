import React from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";

function Home(props) {
        return (
            <div className="tab-container">
                <Tab.Container id="left-tabs-example" defaultActiveKey="first" className="tab-container">
                    <Row>
                        <Col sm={3}>
                            <Nav variant="pills" className="flex-column">
                                <Nav.Item>
                                    <Nav.Link eventKey="first">Bookmarks</Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey="second">MeSH 2 Gene</Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey="third">Gene 2 MeSH</Nav.Link>
                                </Nav.Item>
                            </Nav>
                        </Col>
                        <Col sm={9}>
                            <Tab.Content>
                                <Tab.Pane eventKey="first">
                                    <p>Bookmark Tab</p>
                                </Tab.Pane>
                                <Tab.Pane eventKey="second">
                                    <p>MeSH 2 Gene Tab</p>
                                </Tab.Pane>
                                <Tab.Pane eventKey="third">
                                    <p>Gene 2 MeSH Tab</p>
                                </Tab.Pane>
                            </Tab.Content>
                        </Col>
                    </Row>
                </Tab.Container>
            </div>
        )
}

export default Home;