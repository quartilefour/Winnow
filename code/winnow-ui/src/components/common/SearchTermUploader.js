import React, {useState} from "react";
import {Form} from "react-bootstrap";
import PageLoader from "./PageLoader";
import {QUERY_FORMATS as QF} from "../../constants";

function SearchTermUploader(props) {
    const [batchQueryFormat, setBatchQueryFormat] = useState('');
    const [textareaData, setTextareaData] = useState(null);
    const [uploadFile, setUploadFile] = useState(null);

    /**
     * Updating parent component with data and/or searchable is creating an infinite loop.
     */
    React.useEffect(() => {
        if (batchQueryFormat !== '') {
            if (uploadFile !== null && uploadFile.name.length > 0) {
                console.info(`SearchTermUploader: uploadFile state change: ${batchQueryFormat} - ${uploadFile.name}`)
                //let data = new FormData()
                //data.append('file', uploadFile)
                //data.append('queryFormat', batchQueryFormat)
                //props.update(batchQueryFormat, data, true)
                props.update(batchQueryFormat, uploadFile, true)
                props.searchable(true);
                console.info(`SearchTermUploader: file U pass: ${batchQueryFormat} - ${uploadFile.name}`)
            } else if (textareaData !== null) {
                props.update(batchQueryFormat, textareaData)
                props.searchable(true);
            }
        }
    }, [props, batchQueryFormat, uploadFile, textareaData])

    if (props.active) {
            return (
                <div>
                    <Form id="batch-import">
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
                                type="file"
                                name="fileUpload"
                                onChange={(e) => {
                                    const { target } = e
                                    if (target.value.length > 0) {
                                        setUploadFile(target.files[0])
                                    } else {
                                        target.reset();
                                    }
                                    console.info(`SearchTermUploader: fileUpload <: ${e.currentTarget.files[0].name}`);
                                    setUploadFile(e.currentTarget.files[0]);
                                    //console.info(`SearchTermUploader: fileUpload >: ${uploadFile.name}`);
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
                                disabled={uploadFile !== null}
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