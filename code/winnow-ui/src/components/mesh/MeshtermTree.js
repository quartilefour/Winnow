import React, {useEffect, useState} from 'react';
import {Spinner} from "react-bootstrap";
import 'react-super-treeview/dist/style.css';
import {fetchMeshtermCat, fetchMeshtermNode, fetchMeshtermTree, mapMeshtermTreeData} from "../../service/ApiService";
import cloneDeep from "lodash/cloneDeep";
import SuperTreeview from "react-super-treeview";
import PageLoader from "../common/PageLoader";

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
    const [isLoaded, setIsLoaded] = useState(false);
    const [meshData, setMeshData] = useState({});
    const [checked, setChecked] = useState([]);

    useEffect(() => {
        /* Fetch top level MeSH term categories */
        fetchMeshtermCat()
            .then(res => {
                let mappedData = res.map((mesh, index) => {
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
            }).catch(err => {
            setIsLoaded(true);
        });
    }, []);

    /* Recursive function to find proper parent and add fetched children */
    function navigateNode(indices, updatedData, mappedData) {
        let index = parseInt(indices.shift());
        if (indices.length === 0) {
            updatedData[index].isChildrenLoading = false;
            updatedData[index].isExpanded = true;
            updatedData[index].children = mappedData;
        } else {
           navigateNode(indices, updatedData[index].children, mappedData);
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
            let sscn = sessionStorage.getItem('mtt');
            if (node.isChecked) {
                console.debug(`MeshtermTree updateCN node ${node.meshId} checked`);
                setChecked([...checked, node.meshId]);
                if (sscn === null) {
                    sessionStorage.setItem('mtt', `${node.id}`)
                } else {
                    sessionStorage.setItem('mtt', `${sscn},${node.id}`)
                }
            } else {
                let sscnArray = sscn.split(",");
                console.debug(`MeshtermTree updateCN node ${node.meshId} unchecked`);
                setChecked(checked.filter(term => term !== node.meshId));
                sessionStorage.setItem('mtt', `${sscnArray.filter(term => term !== node.id)}`)
            }
            props.callback(sessionStorage.getItem('mtt').split(','))
        }
    }

    /* Displays MeSH term tree, dynamically populating/removing children as expanded/collapsed */
    if (isLoaded) {
        return (
            <SuperTreeview
                loadingElement={<Spinner animation="border" size="sm" variant="info"/>}
                data={meshData}
                onChange={(e) => {
                    console.info(`MeshtermTree onChange fired`);
                }}
                onUpdateCb={(updatedData) => {
                    setMeshData(updatedData);
                }}
                isDeletable={(node, depth) => {
                    return false;
                }}
                isExpandable={(node, depth) => {
                    return node.hasChild;
                }}
                onCheckToggleCb={(nodes, depth)=>{
                    console.debug(`MeshtermTree checkToggle: ${JSON.stringify(nodes)}`);
                    const checkState = nodes[0].isChecked;

                    applyCheckStateTo(nodes);

                    /* Recursively checks/unchecks immediate children */
                    function applyCheckStateTo(nodes){
                        nodes.forEach((node)=>{
                            node.isChecked = checkState;
                             updatesCheckedNodes(node);
                            if(node.children){
                                applyCheckStateTo(node.children);
                            }
                        })
                    }
                }}
                onExpandToggleCb={(node, depth) => {
                    if (node.isExpanded === true) {
                        console.debug(`MeshtermTree expanding: ${node.name}`);
                        node.isChildrenLoading = true;

                        if (depth === 0) {
                            fetchMeshtermNode(node.id)
                                .then(res => {
                                    insertChildNodes(node, depth, mapMeshtermTreeData(res, node, depth));
                                }).catch(err => {
                                console.debug(`MeshtermTree Error: ${err}`);
                                console.debug(`MeshtermTree Error: ${JSON.stringify(err)}`);
                            })
                        } else {
                            fetchMeshtermTree(node.id)
                                .then(res => {
                                    insertChildNodes(node, depth, mapMeshtermTreeData(res, node, depth));
                                }).catch(err => {
                                console.debug(`MeshtermTree Error: ${err}`);
                                console.debug(`MeshtermTree Error: ${JSON.stringify(err)}`);
                            })
                        }
                    }
                }}
            />
        )
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}