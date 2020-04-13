import React, {useEffect, useState} from "react";
import {Modal, Form, Button, Alert} from "react-bootstrap";
import {saveUserBookmark} from "../../service/ApiService";

function SaveSearchModal(props) {

    const [bookmarkName, setBookmarkName] = useState('');
    const [saveEnabled, setSaveEnabled] = useState(false);
    const [saveBookmark, setSaveBookmark] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    useEffect(() => {
        setSaveEnabled(bookmarkName.length >= 1);
        if (saveBookmark) {
            saveUserBookmark({
                searchName: bookmarkName.slice(0,19),
                searchQuery: props.searchdata.searchQuery,
                queryType: props.searchdata.queryType,
                queryFormat: props.searchdata.queryFormat
            })
                .then( res =>{
                    console.info(`Saved bookmark named: ${bookmarkName}`);
                    setSaveBookmark(false);
                    props.onHide()
                })
                .catch(err => {
                    setError(err);
                    setAlertType('danger')
                })
        }
    }, [props, saveBookmark, bookmarkName]);


    return (
        <Modal
            {...props}
            size="sm"
            aria-labelledby="contained-modal-title-vcenter"
            centered
        >
            <Modal.Header closeButton>
                <Modal.Title>Save Search</Modal.Title>
                <Alert variant={alertType} size="sm">{error}</Alert>
            </Modal.Header>
            <Modal.Body>
                <div>{props.searchdata.results.length} record(s)</div>
                <Form>
                    <Form.Group>
                        <Form.Label>Bookmark Name</Form.Label>
                        <Form.Control
                            id="bm-input"
                            type="text"
                            onChange={e => {
                                setBookmarkName(e.target.value);
                            }}
                        />
                    </Form.Group>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" size="sm" onClick={props.onHide}>
                    Cancel
                </Button>
                <Button
                    variant="info"
                    size="sm"
                    disabled={!saveEnabled}
                    onClick={() => {setSaveBookmark(true)}}
                >
                    Save
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default SaveSearchModal;