import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Modal, Spinner} from "react-bootstrap";
import 'react-super-treeview/dist/style.css';
import {callAPI, parseAPIError} from "../../service/ApiService";
import SuperTreeview from "react-super-treeview";
import PageLoader from "../common/PageLoader";
import {API_RESOURCES} from "../../constants";
import {addMeshterm, fetchMeshterm, removeMeshterm} from "../../service/SearchService";

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
    const [showBM, setShowBM] = useState(false);

    React.useEffect(() => {
        setShowBM(showBM)
        console.info(`MeshtermTree: showBM effect: ${showBM}`)
    }, [showBM])

    React.useEffect(() => {
        console.warn(`meshtermtree: rerender!`)
        removeMeshterm()
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

    //let checkedTerms = new Set();

    /* Updated session object with checked MeSH tree terms and updates parent component */
    function updatesCheckedNodes(node) {
        // changed to check that node.id !== "" as state was getting "" added to it
        if (node && node.id !== null && node.id) {
            if (node.isChecked) {
                console.debug(`MeshtermTree updateCN node ${node.id} checked`);
                addMeshterm(node.id)
            } else {
                console.debug(`MeshtermTree updateCN node ${node.id} unchecked`);
                removeMeshterm(node.id)
            }
        }
    }

    function applyCheckStateToAllNodes(nodes, depth, checkState) {
        for (let n = 0; n < nodes.length; n = n + 1) {
            nodes[n].isChecked = checkState;
            /* Only set isExpanded if node has children */
            nodes[n].isExpanded = (checkState && nodes[n].children.length > 0);

            updatesCheckedNodes(nodes[n]);

            if (nodes[n].children.length > 0) {
                for (let i = 0; i < nodes[n].children.length; i = i + 1) {
                    nodes[n].children[i].isChecked = checkState;
                    nodes[n].children[i].isExpanded = (nodes[n].children[i].children.length > 0);
                }
                applyCheckStateToAllNodes(nodes[n].children, depth + 1, checkState);
            }
        }
        /* Update array of checked MeSH terms in parent for search */
        callback([...fetchMeshterm()]);
    }

    function addEl() {
        let test = document.createElement("div")
        test.setAttribute("id", "waitModal");
        test.innerText = 'Help me!'
        let myNode = document.getElementById('meshterm-tree')
        console.info(`MeshtermTree find add element node:`)
        myNode.parentElement.prepend(test)
    }

    /* Displays MeSH term tree, dynamically populating/removing children as expanded/collapsed */
    if (isLoaded) return (
        <div>
            <Modal show={showBM}>Stand By</Modal>
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
                    applyCheckStateToAllNodes(nodes, depth, checkState)
                }}
            />
        </div>
    )
    return (<div><PageLoader/></div>)
}