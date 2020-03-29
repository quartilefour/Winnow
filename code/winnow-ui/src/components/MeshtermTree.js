import React, {useEffect, useState} from 'react';
import 'react-super-treeview/dist/style.css';
import {fetchMeshtermCat, fetchMeshtermNode, fetchMeshtermTree, mapMeshtermTreeData} from "../service/ApiService";
import cloneDeep from "lodash/cloneDeep";
import SuperTreeview from "react-super-treeview";


export function MeshtermTree(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [meshData, setMeshData] = useState({});
    const [checked, setChecked] = useState([]);

    useEffect(() => {
        fetchMeshtermCat()
            .then(res => {
                let mappedData = res.map((mesh, index) => {
                    return {
                        children: [],
                        id: mesh.categoryId.trim(),
                        meshIndex: index,
                        name: mesh.name.trim(),
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

    if (isLoaded) {
        return (
            <SuperTreeview
                data={meshData}
                onUpdateCb={(updatedData) => {
                    setMeshData(updatedData);
                    props.callback(checked)
                }}
                isDeletable={(node, depth) => {
                    return false;
                }}
                isExpandable={(node, depth) => {
                    return node.hasChild;
                }}
                onCheckToggleCb={(nodes)=>{
                    const checkState = nodes[0].isChecked;

                    applyCheckStateTo(nodes);

                    function applyCheckStateTo(nodes){
                        nodes.forEach((node)=>{
                            node.isChecked = checkState;
                            if (checkState) {
                                console.info(`MeshtermTree2 node ${node.id} checked`);
                               setChecked([...checked, node.id]);
                            } else {
                                console.info(`MeshtermTree2 node ${node.id} unchecked`);
                                setChecked(checked.filter(term => term !== node.id));
                            }
                            if(node.children){
                                applyCheckStateTo(node.children);
                            }
                            console.info(`MeshtermTree2 checked nodes: ${checked}`);
                        })
                    }
                }}
                onExpandToggleCb={(node, depth) => {
                    if (node.isExpanded === true) {
                        node.isChildrenLoading = true;

                        if (depth === 0) {
                            fetchMeshtermNode(node.id)
                                .then(res => {
                                    insertChildNodes(node, depth, mapMeshtermTreeData(res, node, depth));
                                }).catch(err => {
                                console.debug(`MeshtermTree2 Error: ${err}`);
                                console.debug(`MeshtermTree2 Error: ${JSON.stringify(err)}`);
                            })
                        } else {
                            fetchMeshtermTree(node.id)
                                .then(res => {
                                    insertChildNodes(node, depth, mapMeshtermTreeData(res, node, depth));
                                }).catch(err => {
                                console.debug(`MeshtermTree2 Error: ${err}`);
                                console.debug(`MeshtermTree2 Error: ${JSON.stringify(err)}`);
                            })
                        }
                    }
                }}
            />
        )
    } else {
        return (
            <div>Loading...</div>
        )
    }
}