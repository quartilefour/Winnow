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

    const queryFormatRadio = () => {
        console.info(`SearchTermUploader: radios ${props.qType}`);
        switch(props.qType) {
            case 'mesh':
                return (
                    <div>
                        <Form.Check
                            inline
                            type="radio"
                            label="MeSH Id"
                            name="queryFormat"
                            value="meshid"
                        />
                        <Form.Check
                            inline
                            type="radio"
                            label="MeSH Tree Id"
                            name="queryFormat"
                            value="meshtreeid"
                        />
                        <Form.Check
                            inline
                            type="radio"
                            label="MeSH Term"
                            name="queryFormat"
                            value="meshname"
                        />
                    </div>
                );
            case 'gene':
                return (
                    <div>
                        <Form.Check
                            inline
                            type="radio"
                            label="Gene Id"
                            name="queryFormat"
                            value="geneid"
                        />
                        <Form.Check
                            inline
                            type="radio"
                            label="Gene Symbol"
                            name="queryFormat"
                            value="genesymbol"
                        />
                    </div>
                );
            default:
                return null
        }
    };

    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        {queryFormatRadio()}
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