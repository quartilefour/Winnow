import React, {useState} from "react";
import PropTypes from 'prop-types';
import {Form} from "react-bootstrap";
import PageLoader from "./PageLoader";
import {QUERY_FORMATS as QF} from "../../constants";

function SearchTermUploader(props) {

    SearchTermUploader.propTypes = {
        active: PropTypes.bool,
        searchable: PropTypes.func,
        update: PropTypes.func
    }

    const {active, searchable, update} = props;

    const [batchQueryFormat, setBatchQueryFormat] = useState('');
    const [textareaData, setTextareaData] = useState(null);
    const [uploadFile, setUploadFile] = useState(null);

    React.useEffect(() => {
        if (batchQueryFormat !== ''
            && (
                (uploadFile !== null && uploadFile.name.length > 0)
                || textareaData !== null
            )
        ) {
            searchable(true);
        }
    })

    function handleChange(e) {
        switch (e.name) {
            case 'fileUpload':
                setUploadFile(e.files[0])
                update(batchQueryFormat, e.files[0], true)
                break;
            case 'textArea':
                setTextareaData(e.value)
                update(batchQueryFormat, e.value)
                break;
            case 'queryFormat':
                setBatchQueryFormat(e.value)
                if (uploadFile !== null && uploadFile.name.length > 0) {
                    update(e.value, uploadFile, true)
                } else if (textareaData !== null) {
                    update(e.value, textareaData)
                }
                break
            default:
        }
    }

    if (active) return (
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
    return (<div><PageLoader/></div>)
}

export default SearchTermUploader;