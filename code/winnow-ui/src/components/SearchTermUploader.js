import React, {useEffect, useState} from "react";
import {Form, FormFile, Table} from "react-bootstrap";
import {fetchSearchResults} from "../service/ApiService";
import PageLoader from "./common/PageLoader";

function SearchTermUploader(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [resultData, setResultData] = useState('');
    const [haveResults, setHaveResults] = useState(false);
    const [batchQueryType, setBatchQueryType] = useState('');
    const [batchQueryFormat, setBatchQueryFormat] = useState('');

    useEffect(() => {
        if (!haveResults) {
            /* Render file upload/textarea */
            setIsLoaded(true);
        } else {
            /* Process input */
            fetchSearchResults({
                searchQuery: ["D0000012", "D0001234", "D000928326"],
                queryType: batchQueryType,
                queryFormat: batchQueryFormat
            })
                .then(res => {
                    setResultData(res);
                    setIsLoaded(true);
                }).catch(err => {
                setIsLoaded(true);
            });
            //setResultData(props.resData);
            setIsLoaded(true);
        }
    }, [haveResults, props, batchQueryType, batchQueryFormat]);

    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        <Form.Check
                            inline
                            type="radio"
                            label="MeSH Id"
                            name="queryFormat"
                        />
                        <Form.Check
                            inline
                            type="radio"
                            label="MeSH Term"
                            name="queryFormat"
                        />
                        <Form.Group>
                            <Form.Control
                                id="fileUpload"
                                type="file"
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>Manual Entry</Form.Label>
                            <Form.Control as={"textarea"} rows={"15"}/>
                        </Form.Group>
                    </Form>
                </div>
            )
        } else {
            return (
                <div>Placeholder for Results</div>
            )
        }
    } else {
        return (
            <div><PageLoader/></div>
        )
    }
}

export default SearchTermUploader;