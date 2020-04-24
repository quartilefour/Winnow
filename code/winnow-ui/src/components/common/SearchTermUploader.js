import React, {useState} from "react";
import {Form} from "react-bootstrap";
import PageLoader from "./PageLoader";
import {QUERY_FORMATS as QF} from "../../constants";

function SearchTermUploader(props) {
    const [isLoaded, setIsLoaded] = useState(false);
    const [batchQueryFormat, setBatchQueryFormat] = useState('');
    const [textareaData, setTextareaData] = useState(null);
    const [uploadFile, setUploadFile] = useState(null);

    React.useEffect(() => {
        if (textareaData !== null && batchQueryFormat) {
            console.info(`SearchTermUploader: queryFormat: ${batchQueryFormat}`)
            /* Render file upload/textarea */
            props.searchable(batchQueryFormat);
            props.update(batchQueryFormat, textareaData)
            console.info(`SearchTermUploader: textarea U: ${batchQueryFormat} - ${textareaData}`)
        }
        setIsLoaded(true);
    }, [props, batchQueryFormat, textareaData]);

    React.useEffect(() => {
        console.info(`SearchTermUploader: file U: ${batchQueryFormat} - ${JSON.stringify(uploadFile)}`)
        if (uploadFile !== null && uploadFile.length > 0) {
            let data = new FormData()
            data.append('file', uploadFile)
            data.append('queryFormat', batchQueryFormat)
            props.update(batchQueryFormat, data, true)
            props.searchable(true);
            console.info(`SearchTermUploader: file U pass: ${batchQueryFormat} - ${uploadFile}`)
        }
        setIsLoaded(true);
    }, [props, batchQueryFormat, uploadFile])

    if (isLoaded) {
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
                            <Form.Control
                                as="input"
                                type="file"
                                name="fileUpload"
                                onChange={e => {
                                    console.info(`SearchTermUploader: fileUpload <: ${e.target.files[0]}`)
                                    setUploadFile(e.target.files[0])
                                    console.info(`SearchTermUploader: fileUpload >: ${uploadFile}`)
                                }}
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
            <div><PageLoader/></div>
        )
    }
}

export default SearchTermUploader;