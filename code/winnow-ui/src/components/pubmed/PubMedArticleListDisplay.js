import React, {useEffect, useState} from "react";
import {Form, Table, Alert} from "react-bootstrap";
import {fetchPubMedArticleList} from "../../service/ApiService";
import PageLoader from "../common/PageLoader";
import {PUBMED_BASE_URL} from "../../constants";

/**
 * PubMedArticleListDisplay displays a list of PubMed articles found in a previous search.
 *
 * @param props
 * @returns {*}
 * @constructor
 */
function PubMedArticleListDisplay(props) {

    const [isLoaded, setIsLoaded] = useState(false);
    const [listData, setListData] = useState('');
    const [pubmedData, setPubmedData] = useState('');
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');
    const [haveResults, setHaveResults] = useState(false);

    /**
     * extract gene/mesh info from listData.
     * new state var for pubmed results.
     */
    useEffect(() => {
        console.info(`PubMedArticleListDisplay fetching for: ${JSON.stringify(props.listData)}`);
        setListData(props.listData);
        fetchPubMedArticleList(
            {
                geneId: props.listData.geneId,
                meshId: props.listData.meshId,
                symbol: props.listData.symbol,
                name: props.listData.name
            })
            .then(res => {
                setPubmedData(res);
                setIsLoaded(true);
                setHaveResults(true);
            }).catch(err => {
                setError(err);
                setAlertType('danger');
            setIsLoaded(true);
        });
    }, [haveResults, props, listData]);

    if (isLoaded) {
        console.info(`PubMedArticleListDisplay selected: ${JSON.stringify(listData)}`);
        return (
            <div>
                <Alert variant={alertType}>{error}</Alert>
                <Form>
                    <h3> Publications for {listData.symbol} ({listData.geneId}) and {listData.name} ({listData.meshId})</h3>
                    <Table striped bordered hover>
                        <thead>
                        <tr>
                            <th>Title</th>
                            <th>Author</th>
                            <th>Publication Date</th>
                            <th>Publication Link</th>
                        </tr>
                        </thead>
                        <tbody>
                        {pubmedData.results.map((value, index) => {
                            return (
                                <tr key={index}>
                                    <td title={value.title}
                                        style={{
                                        border: 0,
                                        display: "inline-block",
                                        width: "auto",
                                        maxWidth: "250px",
                                        overflow: "hidden",
                                        textOverflow: "ellipsis",
                                        whiteSpace: "nowrap",
                                    }}>{value.title}</td>
                                    <td>{
                                        value.authors.map((author) => {
                                           return (
                                               author.lastName
                                           )
                                        }).join(", ")
                                    }</td>
                                    <td>{value.completedDate}</td>
                                    <td><a target="_blank"
                                           rel="noopener noreferrer"
                                           href={`${PUBMED_BASE_URL}/${value.publicationId}`}>PubMed Article #{value.publicationId}</a>
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </Table>
                </Form>
            </div>
        );
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default PubMedArticleListDisplay;
