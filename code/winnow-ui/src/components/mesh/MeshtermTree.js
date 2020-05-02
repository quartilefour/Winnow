import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Spinner} from "react-bootstrap";
import 'react-super-treeview/dist/style.css';
import {
    callAPI,
    parseAPIError
} from "../../service/ApiService";
import cloneDeep from "lodash/cloneDeep";
import SuperTreeview from "react-super-treeview";
import PageLoader from "../common/PageLoader";
import {API_RESOURCES} from "../../constants";

/**
 * Dynamically generates MeSH term tree
 *
 * Uses https://github.com/azizali/react-super-treeview with heavy customizations to
 * dynamically build MeSH term hierarchy.
 *
 * @param props
 * @returns {*}
 */

export function MeshtermTree(props) {

    MeshtermTree.propTypes = {
        callback: PropTypes.func
    }

    const {callback} = props;

    const {GET_MESH_CAT, GET_MESH_PARENT, GET_MESH_NODE} = API_RESOURCES;

    const [isLoaded, setIsLoaded] = useState(false);
    const [meshData, setMeshData] = useState({});
    const [checked, setChecked] = useState([]);

    React.useEffect(() => {
        /* Fetch top level MeSH term categories */
        callAPI(GET_MESH_CAT)
            .then(res => {
                let mappedData = res.data.map((mesh, index) => {
                    return {
                        children: [],
                        id: mesh.categoryId,
                        meshIndex: index,
                        name: `${mesh.name} [${mesh.categoryId}]`,
                        hasChild: true
                    };
                });
                setMeshData(mappedData);
                setIsLoaded(true);
            })
            .catch(error => {
                console.debug(`Search failed with fatal error.\n${parseAPIError(error)}`);
                setIsLoaded(true);
            });
    }, [GET_MESH_CAT]);

    /**
     * Helper function to map MeSH term data to tree view node.
     *
     * @param data - JSON object of MeSH term.
     * @param node - Tree view node.
     * @param depth - Tree view depth.
     * @return JSON object of Tree node to insert.
     */
    const mapMeshtermTreeData = (data, node, depth) => {
        return data.map((mesh, index) => {
            let id = (depth > 0)
                ? `${mesh.treeParentId}.${mesh.treeNodeId}`
                : `${mesh.treeParentId}${mesh.treeNodeId}`;
            return {
                children: [],
                id: id,
                isChecked: node.isChecked,
                meshIndex: `${node.meshIndex}:${index}`,
                meshId: mesh.meshId,
                hasChild: mesh.hasChild,
                name: `${mesh.meshName} [${id}]`,
            };
        });
    };

    /* Recursive function to find proper parent and add fetched children */
    function navigateNode(indices, updatedData, mappedData) {
        let index = parseInt(indices.shift());
        if (updatedData !== undefined && updatedData[index] !== undefined) {
            if (indices.length === 0) {
                updatedData[index].isChildrenLoading = false;
                updatedData[index].isExpanded = true;
                updatedData[index].children = mappedData;
            } else {
                //console.log(`calling navigateNode with updatedData ${JSON.stringify(updatedData[index].children)}`);
                navigateNode(indices, updatedData[index].children, mappedData);
            }
        }
    }

    /* Function to build updated tree structure */
    function insertChildNodes(node, depth, mappedData) {
        let updatedData = cloneDeep(meshData);
        let indices = node.meshIndex.toString().split(":");
        navigateNode(indices, updatedData, mappedData);
        setMeshData(updatedData);
        setIsLoaded(true);
    }

    /* Updated session object with checked MeSH tree terms and updates parent component */
    function updatesCheckedNodes(node) {
        if (node && node.id !== null) {
            //console.log(`updating state for ${node.id}`);
            if (node && node.id !== null) {
                let sscn = sessionStorage.getItem('mtt');
                if (node.isChecked && !checked.includes(`${node.meshId}`)) {
                    //console.debug(`MeshtermTree updateCN node ${node.meshId} checked`);
                    setChecked([...checked, node.meshId]);
                    if (sscn === null) {
                        sessionStorage.setItem('mtt', `${node.id}`)
                    } else {
                        sessionStorage.setItem('mtt', `${sscn},${node.id}`)
                    }
                } else {
                    let sscnArray = sscn.split(",");
                    //console.debug(`MeshtermTree updateCN node ${node.meshId} unchecked`);
                    setChecked(checked.filter(term => term !== node.meshId));
                    sessionStorage.setItem('mtt', `${sscnArray.filter(term => term !== node.id)}`)
                }
                callback([...new Set(sessionStorage.getItem('mtt').split(','))])
            }
        }
    }

    function getAllChildren(node, depth) {
        //console.log(`inside getAllChildren ${JSON.stringify(node)} depth ${depth}`);
        if (node.hasChild) {
            //console.log(`inside getAllChildren, node has child`);
            if (depth === 0) {
                //console.log(`inside getAllChildren, node has child and depth === 0`);
                callAPI(GET_MESH_NODE, node.id)
                    .then(res => {
                        //console.log(`inside getAllChildren node id ${JSON.stringify(res)}`);

                        // all the children
                        let newNodes = getNewNode(mapMeshtermTreeData(res.data, node, depth));
                        node.children = newNodes;
                        expandAllNodes(node, depth);
                        //console.log(`node ${node.id} has children ${JSON.stringify(node.children)}`);
                        newNodes.forEach((node) => {
                            //console.log(`newNode for ${node.id} is ${JSON.stringify(node)}`);
                            //console.log(`newNode is ${JSON.stringify(node)}`);
                            getAllChildren(node, depth + 1);
                            return node;
                        })
                    }).catch(err => {
                    console.log(`MeshtermTree Error: ${err}`);
                    console.log(`MeshtermTree Error: ${JSON.stringify(err)}`);
                })
            } else {
                callAPI(GET_MESH_PARENT, node.id)
                    .then(res => {
                        //console.log(`inside leaf getAllChildren for ${node.id}`);
                        let newNodes = getNewNode(mapMeshtermTreeData(res.data, node, depth));
                        node.children = newNodes;
                        //console.log(`node ${node.id} has children ${JSON.stringify(node.children)}`);
                        newNodes.forEach((node) => {
                            //console.log(`newNode for ${node.id} is ${JSON.stringify(node)}`);
                            //console.log(`newNode is ${JSON.stringify(node)}`);
                            getAllChildren(node, depth + 1);
                            return node;
                        })
                    }).catch(err => {
                    console.log(`MeshtermTree Error: ${err}`);
                    console.log(`MeshtermTree Error: ${JSON.stringify(err)}`);
                })
            }
        }
        return node;
    }

    function getNewNode(mappedData) {
        if (mappedData) {
            let newNodes = []
            //console.log(`mappedData exists ${JSON.stringify(mappedData)}`);
            // create actual nodes for the new data, so that we can find their children next
            for (let i = 0; i < mappedData.length; i = i + 1) {
                newNodes[i] = {
                    "children": [],
                    "id": mappedData[i].id,
                    "isChecked": mappedData[i].isChecked,
                    "isExpanded": mappedData[i].isChecked,
                    "meshIndex": `${mappedData[i].meshIndex}:${i}`,
                    "meshId": mappedData[i].meshId,
                    "hasChild": mappedData[i].hasChild,
                    "name": `${mappedData[i].name} [${i}]`,
                };
                updatesCheckedNodes(newNodes[i]);
            }
            return newNodes;
        }
    }

    function expandAllNodes(node, depth) {
        if (node.isExpanded === true) {
            //console.debug(`MeshtermTree expanding: ${node.name}`);
            node.isChildrenLoading = true;

            if (depth === 0) {
                callAPI(GET_MESH_NODE, node.id)
                    .then(res => {
                        insertChildNodes(node, depth, mapMeshtermTreeData(res.data, node, depth));
                    }).catch(err => {
                    console.debug(`MeshtermTree Error: ${err}`);
                    console.debug(`MeshtermTree Error: ${JSON.stringify(err)}`);
                })
            } else {
                callAPI(GET_MESH_PARENT, node.id)
                    .then(res => {
                        insertChildNodes(node, depth, mapMeshtermTreeData(res.data, node, depth));
                    }).catch(err => {
                    console.debug(`MeshtermTree Error: ${err}`);
                    console.debug(`MeshtermTree Error: ${JSON.stringify(err)}`);
                })
            }
        }
    }


    /* Displays MeSH term tree, dynamically populating/removing children as expanded/collapsed */
    if (isLoaded) return (
        <SuperTreeview
            loadingElement={<Spinner animation="border" size="sm" variant="info"/>}
            data={meshData}
            onUpdateCb={(updatedData) => {
                setMeshData(updatedData);
            }}
            isDeletable={(node, depth) => {
                return false;
            }}
            isExpandable={(node, depth) => {
                return node.hasChild;
            }}
            onCheckToggleCb={(nodes, depth) => {
                //console.debug(`MeshtermTree checkToggle: ${JSON.stringify(nodes)}`);
                const checkState = nodes[0].isChecked;
                nodes[0].isExpanded = checkState;


                /* Recursively checks/unchecks immediate children */
                //console.debug(`MeshtermTree checkToggle: ${JSON.stringify(nodes)}`);

                applyCheckStateToAllNodes(nodes, depth);

                // expand all the nodes under the top checked node
                expandAllNodes(nodes[0], depth);

                function applyCheckStateToAllNodes(nodes, depth) {
                    nodes.forEach((node) => {
                        applyCheckStateTo(node, depth);
                    })
                }


                /* Recursively checks/unchecks immediate children */
                function applyCheckStateTo(node, depth) {
                    node.isChecked = checkState;
                    //console.log(`check state is ${node.isChecked}`);
                    if (node.hasChild) {
                        let allChildren = getAllChildren(node, depth);
                        updatesCheckedNodes(node);
                        if (allChildren.children) {
                            allChildren.children.forEach((node) => {
                                applyCheckStateTo(node, depth + 1);
                            })
                        }
                    }
                }
            }}
            onExpandToggleCb={(node, depth) => {
                expandAllNodes(node, depth);
            }}

        />
    )
    return (<div><PageLoader/></div>)
}