import React, {useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import BookmarkTab from "./BookmarkTab";
import Mesh2GeneTab from "./Mesh2GeneTab";
import Gene2MeshTab from "./Gene2MeshTab";
import ApiService from "../service/ApiService";
import {Error} from "./HTMLElements";

/**
 * Renders the Dashboard landing page for authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Dashboard(props) {

    const [error, setError] = useState('');
    const [geneData, setGeneData] = useState([]);

    /**
     * Retrieves list of Genes for selection. Maps returned Gene data
     * keys to to keynames expected by react-select.
     */
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
            setError(error);
        })
    }

    return (
        <div className="tab-container">
            <Error>{error}</Error>
            <Tab.Container id="left-tabs-example" defaultActiveKey="gene2mesh" className="tab-container">
                <Row>
                    <Col sm={3}>
                        <Nav variant="pills" className="flex-column">
                            <Nav.Item>
                                <Nav.Link eventKey="bookmarks">Bookmarks</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="mesh2gene">MeSH 2 Gene</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="gene2mesh">Gene 2 MeSH</Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Col>
                    <Col sm={9}>
                        <Tab.Content>
                            <Tab.Pane eventKey="bookmarks">
                                <p>Bookmark Tab</p>
                                <BookmarkTab bookmarkData={null}/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="mesh2gene">
                                <p>MeSH 2 Gene Tab</p>
                                <Mesh2GeneTab meshData={null}/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="gene2mesh" onClick={getGenes}>
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