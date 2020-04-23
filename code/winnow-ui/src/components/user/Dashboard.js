import React, {useEffect, useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import BookmarkTab from "./BookmarkTab";
import RecentSearchesTab from "./RecentSearchesTab";
import ComboSearchTab from "../common/ComboSearchTab";

/**
 * Renders the Dashboard landing page for authenticated users.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function Dashboard(props) {
    const [currentTab, setCurrentTab] = useState('bookmarks');

    React.useEffect(() => {
        let storedTab = sessionStorage.getItem('dashboardTab');
        if (storedTab) {
            setCurrentTab(storedTab);
        }
    }, [currentTab]);

    return (
        <div id="dashboard">
            <Tab.Container id="left-tabs"
                           activeKey={currentTab}
                           className="tab-container"
                           mountOnEnter={true}
                           unmountOnExit={true}
                           onSelect={
                               (e) => {
                                   sessionStorage.setItem('dashboardTab', e);
                                   setCurrentTab(e)
                               }
                           }>
                <Row>
                    <Col sm={2} id="dashboard-outer-tabs">
                        <Nav id="dashboard-tabs" variant="tabs" className="flex-column">
                            <Nav.Item>
                                <Nav.Link eventKey="bookmarks">Bookmarks</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="search">Search</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="search-history">Recent Searches</Nav.Link>
                            </Nav.Item>
                        </Nav>
                    </Col>
                    <Col sm={10} id="dashboard-outer-tab-content">
                        <Tab.Content id="dashboard-tab-content">
                            <Tab.Pane eventKey="bookmarks">
                                <p className="tab-heading">User Bookmarks</p>
                                <BookmarkTab/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="search">
                                <p className="tab-heading">Gene/MeSH Search</p>
                                <ComboSearchTab/>
                            </Tab.Pane>
                            <Tab.Pane eventKey="search-history">
                                <p className="tab-heading">Recent Searches</p>
                                <RecentSearchesTab/>
                            </Tab.Pane>
                        </Tab.Content>
                    </Col>
                </Row>
            </Tab.Container>
        </div>
    )
}

export default Dashboard;