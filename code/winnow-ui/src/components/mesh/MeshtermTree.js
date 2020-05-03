import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Modal, Spinner} from "react-bootstrap";
import 'react-super-treeview/dist/style.css';
import {callAPI, parseAPIError} from "../../service/ApiService";
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
        callback: PropTypes.func,
    }

    const {callback} = props;

    const {GET_MESH_TREE} = API_RESOURCES;

    const [isLoaded, setIsLoaded] = useState(false);
    const [meshData, setMeshData] = useState({});

    React.useEffect(() => {
        /* Fetch MeSH term tree */
        callAPI(GET_MESH_TREE)
            .then(res => {
                setMeshData(res.data);
                setIsLoaded(true);
            })
            .catch(error => {
                console.debug(`Search failed with fatal error.\n${parseAPIError(error)}`);
                setIsLoaded(true);
            });
    }, [GET_MESH_TREE]);

    /* Updated session object with checked MeSH tree terms and updates parent component */
    function updatesCheckedNodes(node) {
        // changed to check that node.id !== "" as state was getting "" added to it
        if (node && node.id !== null && node.id) {
            //console.log(`MeshtermTree: updating state for ${node.id}`);
            let sscn = sessionStorage.getItem('mtt');
            if (node.isChecked) {
                console.debug(`MeshtermTree updateCN node ${node.id} checked`);
                if (sscn === null) {
                    sessionStorage.setItem('mtt', `${node.id}`)
                } else {
                    sessionStorage.setItem('mtt', `${sscn},${node.id}`)
                }
            } else {
                let sscnArray = sscn.split(",");
                console.debug(`MeshtermTree updateCN node ${node.id} unchecked`);
                sessionStorage.setItem('mtt', `${sscnArray.filter(term => term !== node.id)}`)
            }
            callback([...new Set(sessionStorage.getItem('mtt').split(','))])
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
        }
    }

    function checkAllChildren(node, depth, checkState) {
        node.isChecked = checkState;
        updatesCheckedNodes(node);
        //console.debug(`isLoading set for ${node.isChildrenLoading}`);
        //console.debug(`the children for node ${node.id} are ${JSON.stringify(node.children)}`)
        if (node.children.length > 0) {
            for (let i = 0; i < node.children.length; i = i + 1) {
                //console.debug(`checking for child ${node.children[i]}`);
                node.children[i].isChecked = checkState;
                //console.log(`calling updatesCheckedNodes from within loop ${node.id}`);
                //updatesCheckedNodes(node.children[i]); // Should be unnecessary as this gets called below
                //console.log(`calling checkAllChildren with ${JSON.stringify(node.children[i])}`);
                checkAllChildren(node.children[i], depth + 1, checkState);
            }
        }
        setIsLoaded(true);
    }

    /* Displays MeSH term tree, dynamically populating/removing children as expanded/collapsed */
    if (isLoaded) return (
        <div>
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
                    return node.children.length > 0;
                }}
                onCheckToggleCb={(nodes, depth) => {
                    const checkState = nodes[0].isChecked;

                    applyCheckStateToAllNodes(nodes, depth, checkState);

                    function applyCheckStateToAllNodes(nodes, depth, checkState) {
                        for (let n = 0; n < nodes.length; n = n + 1) {
                            nodes[n].isChecked = checkState;
                            /* Only set isExpanded if node has children */
                            nodes[n].isExpanded = (checkState && nodes[n].children.length > 0);

                            /* Expand all the nodes under the top checked node and set isChecked */
                            expandAllNodes(nodes[n], depth);
                            checkAllChildren(nodes[n], depth, checkState);

                            if (nodes[n].children.length > 0) {
                                for (let i = 0; i < nodes[n].children.length; i = i + 1) {
                                    applyCheckStateToAllNodes(nodes[n].children[i], depth + 1, checkState);
                                }
                            }
                        }
                    }
                }}
            />
        </div>
    )
    return (<div><PageLoader/></div>)
}