import React, {useState} from "react";
import {Form} from "react-bootstrap";
import PageLoader from "./PageLoader";
import {QUERY_FORMATS as QF} from "../../constants";

function SearchTermUploader(props) {
    const [batchQueryFormat, setBatchQueryFormat] = useState('');
    const [textareaData, setTextareaData] = useState(null);
    const [uploadFile, setUploadFile] = useState(null);

    React.useEffect(() => {
        if (batchQueryFormat !== '') {
            if (uploadFile !== null && uploadFile.name.length > 0) {
                console.info(`SearchTermUploader: uploadFile state change: ${batchQueryFormat} - ${uploadFile.name}`)
                props.searchable(true);
                console.info(`SearchTermUploader: file U pass: ${batchQueryFormat} - ${uploadFile.name}`)
            } else if (textareaData !== null) {
                props.searchable(true);
            }
        }
    })

    function handleChange(e) {
        console.info(`SearchTermUploader: handleChange(): ${e.name}`)
        switch(e.name) {
            case 'fileUpload':
                setUploadFile(e.files[0])
                props.update(batchQueryFormat, e.files[0], true)
                break;
            case 'textArea':
                setTextareaData(e.value)
                props.update(batchQueryFormat, e.value)
                break;
            case 'queryFormat':
                setBatchQueryFormat(e.value)
                if (uploadFile !== null && uploadFile.name.length > 0) {
                    props.update(e.value, uploadFile, true)
                } else if (textareaData !== null) {
                    props.update(e.value, textareaData)
                }
                break
            default:
        }
    }

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
                                        handleChange(e.currentTarget)
                                    }}
                                />
                            );
                        })}
                        <Form.Group>
                            <Form.Control
                                type="file"
                                name="fileUpload"
                                onChange={(e) => {
                                    handleChange(e.currentTarget)
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
                                name="textArea"
                                rows={"10"}
                                onChange={(e) => {
                                    handleChange(e.currentTarget)
                                }}
                                placeholder={`Cystic Fibrosis\nMultiple Endocrine Neoplasia Type 2a`}
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