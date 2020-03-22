import React, {useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import {Form, Input} from "./HTMLElements";
import Gene2MeshTab from "./Gene2MeshTab";
import ApiService from "../service/ApiService";

function Dashboard(props) {

    const [geneData, setGeneData] = useState([]);

    function getGenes() {
        ApiService.getAllGenes().then(res => {
            if (res.status === 200) {
                let mappedData = res.data.map((gene, index) => {
                    return {value: gene.geneId, label: gene.symbol};
                });
                console.info(`Mapped data: ${mappedData[12].value} (${mappedData[12].label})`);
                setGeneData(mappedData);
            }
        }).catch(error => {
            console.error(`showGenes Error: ${error}`);
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
                            <Tab.Pane eventKey="third" onClick={getGenes}>
                                <p>Gene 2 MeSH Tab</p>
                                <Gene2MeshTab geneData={geneData}/>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Dashboard;