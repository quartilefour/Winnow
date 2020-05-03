import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Spinner} from "react-bootstrap";
import {useMeshTree} from "../../context/meshtree";
import 'react-super-treeview/dist/style.css';
import {
    callAPI, parseAPIError
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

    const {GET_MESH_TREE, GET_MESH_CAT, GET_MESH_PARENT, GET_MESH_NODE} = API_RESOURCES;

    const {meshTermTree} = useMeshTree();
    const [isLoaded, setIsLoaded] = useState(false);
    const [meshData, setMeshData] = useState({});
    const [checked, setChecked] = useState([]);

    React.useEffect(() => {
        console.info(`MessTermTree: mesh context: ${JSON.stringify(meshTermTree)}`)
        /*let mappedData = meshTermTree.map((mesh, index) => {
            return {
                children: mesh.c,
                id: mesh.i,
                meshIndex: index,
                name: `${mesh.n} [${mesh.i}] eas`,
                hasChild: mesh.h
            }
        });
        setMeshData(mappedData);
        setIsLoaded(true);*/

        /* Fetch top level MeSH term categories */
        callAPI(GET_MESH_TREE)
            .then(res => {
                /*let mappedData = res.data.map((mesh, index) => {
                    /*return {
                        children: [],
                        id: mesh.categoryId,
                        meshIndex: index,
                        name: `${mesh.name} [${mesh.categoryId}]`,
                        hasChild: true
                    }
                    return {
                        children: mesh.c,
                        id: mesh.i,
                        meshIndex: index,
                        name: `${mesh.n} [${mesh.i}] eas`,
                        hasChild: mesh.h
                    }
                });*/
                console.info(`MessTermTree: mesh data: ${JSON.stringify(res.data)}`)
                setMeshData(res.data);
                setIsLoaded(true);
            })
            .catch(error => {
                console.debug(`Search failed with fatal error.\n${parseAPIError(error)}`);
                setIsLoaded(true);
            });
    }, [GET_MESH_CAT, GET_MESH_TREE, meshTermTree]);

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
        // changed to check that node.id !== "" as state was getting "" added to it
        if (node && node.id !== null && node.id) {
            //console.log(`updating state for ${node.meshId}`);
            //console.log(`updating state for ${node.meshId}`);
            //if (node && node.id !== null) {
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
        //}
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
            //console.debug(`MeshtermTree expanding: ${node.name} and isExpanded is ${node.isExpanded}`);
            node.isChildrenLoading = true; // this doesn't appear to be working
            //console.debug(`isLoading set for ${node.isChildrenLoading}`);

            if (node.children.length > 0) {
                for (let i = 0; i < node.children.length; i = i + 1) {
                    if (node.children[i].children.length > 0) {
                        node.children[i].isExpanded = true;
                        expandAllNodes(node.children[i], depth + 1);
                    }
                }
            }
            node.isChildrenLoading = false;
            setIsLoaded(true);

               /* callAPI(GET_MESH_NODE, node.id)
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
                })*/
            }

    }

    function checkAllChildren(node, depth, checkState) {

        //console.debug(`MeshtermTree checking: ${node.id} and isExpanded is ${node.isExpanded} and checkState is ${node.isChecked}`);
        //console.debug(`full node is ${JSON.stringify(node)}`);
        node.isChecked = checkState;
        updatesCheckedNodes(node);
        //console.debug(`isLoading set for ${node.isChildrenLoading}`);
        //console.debug(`the children for node ${node.id} are ${JSON.stringify(node.children)}`)
        if (node.children.length > 0) {
            for (let i = 0; i < node.children.length; i = i + 1) {
                //console.debug(`checking for child ${node.children[i]}`);
                node.children[i].isChecked = checkState;
                //console.log(`calling updatesCheckedNodes from within loop ${node.id}`);
                updatesCheckedNodes(node.children[i]);
                //console.log(`calling checkAllChildren with ${JSON.stringify(node.children[i])}`);
                checkAllChildren(node.children[i], depth + 1, checkState);
            }
        }


        setIsLoaded(true);
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
                //return node.hasChild;
                return node.children.length > 0;
            }}
            onCheckToggleCb={(nodes, depth) =>{
                const checkState = nodes[0].isChecked;

                applyCheckStateToAllNodes(nodes, depth, checkState);


                function applyCheckStateToAllNodes(nodes, depth, checked) {
                    for (let n = 0; n < nodes.length; n = n + 1){
                        nodes[n].isChecked = checkState;
                        nodes[n].isExpanded = checkState;

                        //expand all the nodes under the top checked node and set isChecked
                        expandAllNodes(nodes[n], depth);
                        checkAllChildren(nodes[n], depth, checked);

                        if (nodes[n].children.length > 0) {
                            for (let i = 0; i < nodes[n].children.length; i = i + 1){
                                applyCheckStateToAllNodes(nodes[n].children[i], depth + 1, checked);
                            }
                        }
                    }
                }
            }}
                /*onCheckToggleCb={(nodes, depth) => {
                    //console.debug(`MeshtermTree checkToggle: ${JSON.stringify(nodes)}`);
                    const checkState = nodes[0].isChecked;
                    nodes[0].isExpanded = checkState;


                    /* Recursively checks/unchecks immediate children */
                //console.debug(`MeshtermTree checkToggle: ${JSON.stringify(nodes)}`);

            //applyCheckStateToAllNodes(nodes, depth);

            // expand all the nodes under the top checked node
            //expandAllNodes(nodes[0], depth);

            /*function applyCheckStateToAllNodes(nodes, depth) {
                nodes.forEach((node) => {
                    node.isChecked = checkState;
                    if (node.children.length > 0) {
                        node.children.forEach((child) => {
                            applyCheckStateToAllNodes(child, depth);
                        })
                    }
                })
            }


            /* Recursively checks/unchecks immediate children */
            /*function applyCheckStateToAllNodes(node, depth) {
                node.isChecked = checkState;
                //console.log(`check state is ${node.isChecked}`);
                if (node.children.length > 0) {
                    //let allChildren = getAllChildren(node, depth);
                    //updatesCheckedNodes(node);
                    //if (allChildren.children) {
                        allChildren.children.forEach((node) => {
                            applyCheckStateTo(node, depth + 1);
                        })
                    }
                }
            }*/
            //}}*/
            onExpandToggleCb={(node, depth) => {
                /*if (node.isExpanded) {
                    node.isExpanded = !(node.isExpanded);
                }
                else {
                    node.isExpanded = true;
                }*/
                expandAllNodes(node, depth);
            }}

        />
    )
    return (<div><PageLoader/></div>)
}