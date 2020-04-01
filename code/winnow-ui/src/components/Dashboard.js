import React, {useEffect, useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import BookmarkTab from "./BookmarkTab";
import Mesh2GeneTab from "./Mesh2GeneTab";
import Gene2MeshTab from "./Gene2MeshTab";
import Cookies from "js-cookie";

/**
 * Renders the Dashboard landing page for authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Dashboard(props) {
    const [currentTab, setCurrentTab] = useState('bookmarks');
    const [isTabSelected, setIsTabSelected] = useState(false);

    useEffect(() => {
        //const dashboardCookie = Cookies.get("WinnowDashboard") ? Cookies.get("WinnowDashboard") : null;
        //console.info(`Dashboard cookie: ${dashboardCookie}`);
        //if (isTabSelected) {
        //   Cookies.set("WinnowDashboard", currentTab)
       // }
        //if (dashboardCookie) {
        //    setCurrentTab(dashboardCookie);
        //    console.info(`Dashboard cookie setting tab to: ${currentTab}`);
        //} else {}

    }, [isTabSelected, currentTab, setCurrentTab]);

    function switchTab(tab) {
        console.info(`Dashboard: clicked tab: ${JSON.stringify(tab)}`);
        setCurrentTab(tab);
        //setIsTabSelected(true);
    }

    return (
        <div id="dashboard">
            <Tab.Container id="left-tabs" defaultActiveKey={currentTab} className="tab-container">
                <Row>
                    <Col sm={3} id="dashboard-outer-tabs">
                        <Nav id="dashboard-tabs" variant="tabs" className="flex-column" onSelect={switchTab}>
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
                    <Col sm={9} id="dashboard-outer-tab-content">
                        <Tab.Content id="dashboard-tab-content">
                            <Tab.Pane eventKey="bookmarks">
                                <p className="tab-heading">User Bookmarks</p>
                                <BookmarkTab />
                            </Tab.Pane>
                            <Tab.Pane eventKey="mesh2gene">
                                <p className="tab-heading">MeSH 2 Gene Search</p>
                                <Mesh2GeneTab />
                            </Tab.Pane>
                            <Tab.Pane eventKey="gene2mesh">
                                <p className="tab-heading">Gene 2 MeSH Search</p>
                                <Gene2MeshTab />
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Dashboard;