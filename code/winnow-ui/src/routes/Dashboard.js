import React, {useRef, useState} from 'react';
import {Nav, Tab, Row, Col} from "react-bootstrap";
import BookmarkTab from "../components/user/BookmarkTab";
import RecentSearchesTab from "../components/user/RecentSearchesTab";
import ComboSearchTab from "../components/common/ComboSearchTab";

/**
 * Renders the Dashboard landing page for authenticated users.
 *
 * @returns {*}
 * @constructor
 */
function Dashboard() {
    const [currentTab, setCurrentTab] = useState('search');

    const searchRef = useRef();

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
                                   if (e === 'search' && currentTab === 'search') {
                                       searchRef.current.returnToSelection()
                                   }
                                   sessionStorage.setItem('dashboardTab', e);
                                   setCurrentTab(e)
                               }
                           }>
                <Row>
                    <Col sm={2} id="dashboard-outer-tabs">
                        <Nav id="dashboard-tabs" variant="tabs" className="flex-column">
                            <Nav.Item>
                                <Nav.Link eventKey="search">SEARCH</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="search-history">RECENT SEARCHES</Nav.Link>
                            </Nav.Item>
                            <Nav.Item>
                                <Nav.Link eventKey="bookmarks">BOOKMARKS</Nav.Link>
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
                                <ComboSearchTab ref={searchRef}/>
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