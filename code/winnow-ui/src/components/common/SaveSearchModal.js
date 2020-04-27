import React, {useState} from "react";
import {Modal, Form, Button, Alert} from "react-bootstrap";
import {parseAPIError, saveUserBookmark} from "../../service/ApiService";

/**
 * Functional component to save a User's current search as a bookmark.
 *
 * @param props
 * @return {*}
 * @constructor
 */
function SaveSearchModal(props) {

    const [bookmarkName, setBookmarkName] = useState('');
    const [saveEnabled, setSaveEnabled] = useState(false);
    const [saveBookmark, setSaveBookmark] = useState(false);
    const [error, setError] = useState('');
    const [alertType, setAlertType] = useState('');

    React.useEffect(() => {
        setSaveEnabled(bookmarkName.length >= 1);
        if (saveBookmark) {
            saveUserBookmark({
                searchName: bookmarkName.slice(0, 19),
                searchQuery: props.searchdata.searchQuery,
                queryType: props.searchdata.queryType,
                queryFormat: props.searchdata.queryFormat
            })
                .then(res => {
                    setSaveBookmark(false);
                    props.onHide()
                })
                .catch(error => {
                    setError(`Could not save bookmark.\n${parseAPIError(error)}`);
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
                <Alert variant={alertType} show={error.length > 0}>{error}</Alert>
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
                    onClick={() => {
                        setSaveBookmark(true)
                    }}
                >
                    Save
                </Button>
            </Modal.Footer>
        </Modal>
    );
}

export default SaveSearchModal;