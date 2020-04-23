import React, {useEffect, useState} from "react";
import {Form} from "react-bootstrap";
import PageLoader from "./PageLoader";
import {QUERY_FORMATS as QF} from "../../constants";

function SearchTermUploader(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [haveResults, setHaveResults] = useState(false);
    const [batchQueryFormat, setBatchQueryFormat] = useState('');
    const [textareaData, setTextareaData] = useState(null);
    const [uploadFile, setUploadFile] = useState('');

    useEffect(() => {
        console.info(`SearchTermUploader: queryFormat: ${batchQueryFormat}`)
        if (!haveResults) {
            /* Render file upload/textarea */
            setIsLoaded(true);
            if (uploadFile.length > 0) {
                let data = new FormData()
                data.append('file', uploadFile)
                data.append('queryFormat', batchQueryFormat)
                props.update(batchQueryFormat, data, true)
                props.searchable(true);
                console.info(`SearchTermUploader: file U: ${batchQueryFormat} - ${uploadFile}`)
            } else {
                props.searchable(textareaData !== null && batchQueryFormat);
                props.update(batchQueryFormat, textareaData)
                console.info(`SearchTermUploader: textarea U: ${batchQueryFormat} - ${textareaData}`)
            }
        } else {
            setIsLoaded(true);
        }
    }, [haveResults, props, batchQueryFormat, textareaData, uploadFile]);

    if (isLoaded) {
        if (!haveResults) {
            return (
                <div>
                    <Form>
                        {Object.keys(QF).map((key) => {
                            return (
                                <Form.Check
                                    key={key}
                                    inline
                                    type="radio"
                                    label={QF[key].label}
                                    name="queryFormat"
                                    value={QF[key].value}
                                    onChange={(e) => {
                                        setBatchQueryFormat(e.currentTarget.value)
                                    }}
                                />
                            );
                        })}
                        <Form.Group>
                            <Form.File
                                id="fileUpload"
                                label="Choose file..."
                                onChange={(e) => {
                                    setUploadFile(e.target.files[0])
                                    console.info(uploadFile)
                                }}
                                custom
                            />
                        </Form.Group>
                        <Form.Group>
                            <Form.Label>
                                Manual Entry
                                <span className="instruction">(One term per line)</span>
                            </Form.Label>
                            <Form.Control
                                as={"textarea"}
                                rows={"10"}
                                onChange={(e) => {
                                    let value = e.target.value;
                                    console.info(`SearchTermUploader: textarea: ${JSON.stringify(value)}`)
                                    setTextareaData(value);
                                }}
                            />
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