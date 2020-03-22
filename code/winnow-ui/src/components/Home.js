import React, {useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import {Button, Form, Input} from "./AuthForm";
import ApiService from "../service/ApiService";

function Home(props) {
    const [geneData, setGeneData] = useState('');
    function showGenes() {
        ApiService.getAllGenes().then(res => {
            if (res.status === 200) {
               setGeneData(`${res.data[0]['geneId']}: ${res.data[0]['symbol']}`);
            }
        }).catch(error => {
            console.error(`showGenes Error: ${error}`)
        })
    }

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
                                <Form>
                                    <Input type="text"/>
                                </Form>
                                <Form>
                                    <Input/>
                                </Form>
                            </Tab.Pane>
                            <Tab.Pane eventKey="third">
                                <p>Gene 2 MeSH Tab</p>
                                <Form>
                                    <Input type="text"/>
                                    <Button onClick={showGenes}>Show Genes</Button>
                                </Form>
                                <div>{geneData}</div>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Home;