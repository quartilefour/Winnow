import React, {useState} from "react";
import {Button, Card, Collapse, Table} from "react-bootstrap";
import matrix from "../img/matrix.png";
import matrixExample from "../img/matrix-example.png";
import rExample from "../img/r-example.png";

function Support() {
    const [open1, setOpen1] = useState(false);
    const [open2, setOpen2] = useState(false);
    const [open3, setOpen3] = useState(false);
    const [open4, setOpen4] = useState(false);
    return (
        <div>
            <Card
                className="text-center tab entry-form"
                style={{
                    flexDirection: 'column',
                    maxWidth: '720px',
                    display: 'flex',
                    margin: '5% auto',
                    width: '75%'
                }}
            >
                <Card.Header>
                    <Card.Title>Winnow Support</Card.Title>
                    <Card.Subtitle>FAQ</Card.Subtitle>
                </Card.Header>
                <Card.Body>
                    <Card.Text>
                        Here in your hour of need.
                    </Card.Text>
                    <div>
                        <Button
                            onClick={() => setOpen1(!open1)}
                            aria-controls="example-collapse-text"
                            aria-expanded={open1}
                            className="faq-button"
                        >
                            What is Winnow?
                        </Button>
                        <Collapse in={open1}>
                            <div>
                                <p></p>
                                <p>
                                    Winnow assists in the literature review process by enabling researchers to query on one or
                                    more gene IDs, gene symbols, or gene descriptions, as well as filter by one or more
                                    Medical Subheadings (MeSH terms). Winnow applies the Chi Square test of independence to
                                    determine whether a given gene and MeSH term are statistically independent.
                                </p>
                                <p>
                                    We also return which genes are reported in the same publications. When the search results are returned, you can click
                                    on the 'gene details' link to view specific details about a gene. This modal also
                                    displays all of the genes that 'co-occur' in publications with the gene that was
                                    clicked on. Any of these 'co-occurring' genes can be clicked on to link out to NCBI
                                    for full details.
                                </p>
                                <p>
                                    Finally, we return all publications that mention specific pairs of genes and MeSH
                                    terms that were queried for. By clicking on the publication count, you can visit a
                                    page populated with these specific publications. This page will display more specific
                                    details about the publication and will also enable you to link out to PubMed for full
                                    article details.
                                </p>
                            </div>
                        </Collapse>
                    </div>
                    <div>
                        <hr/>
                    </div>
                    <div>
                        <Button
                            onClick={() => setOpen2(!open2)}
                            aria-controls="example-collapse-text"
                            aria-expanded={open2}
                            className="faq-button"
                        >
                            What types of searches can I perform using Winnow?
                        </Button>
                        <Collapse in={open2}>
                            <div>
                                <div>
                                    <p></p>
                                    <p className="text-title">Gene Query</p>
                                    <p>
                                        On the search page, you can enter gene queries by gene ID, symbol, or description.
                                        Once you start typing, suggestions will appear for you to click and add to your
                                        search. Once you have built your desired query, you can click search to execute it.
                                        You may also enter a batch upload of your desired genes.
                                    </p>
                                    <hr/>
                                    <p className="text-title">MeSH Query</p>
                                    <p>
                                        On the search page, you can also search for specific MeSH terms or general MeSH
                                        categories, and Winnow will return genes that occured in publications with
                                        those MeSH terms. Please keep in mind that some upper MeSH categories contain
                                        hundreds of terms, if not more, and as such, your result results may be delayed.
                                    </p>
                                    <hr/>
                                    <p className="text-title">Gene - MeSH Query</p>
                                    <p>
                                        Selecting a MeSH term or category after you have entered a gene, or
                                        more, will filter your gene search results to include only those that also
                                        pertain to your selected MeSH terms. Again, note that selecting upper MeSH
                                        categories may result in delayed result times.
                                    </p>
                                </div>
                            </div>
                        </Collapse>
                    </div>
                    <div>
                        <hr/>
                    </div>
                    <div>
                        <Button
                            onClick={() => setOpen3(!open3)}
                            aria-controls="example-collapse-text"
                            aria-expanded={open3}
                            className="faq-button"
                        >
                            What statistical test is used to determine gene-MeSH independence?
                        </Button>
                        <Collapse in={open3}>
                            <div>
                                <p></p>
                                <p>
                                    We use the Chi Square Test of Independence to determine whether a given gene
                                and MeSH term are likely to be independent. The calculation depends upon a
                                contingency matrix, in this application the matrix is 2x2. The counts for the
                                individual cells in the matrix are representative of the number of publications that
                                mention that input gene, the input MeSH, both of these terms, or neither of these terms.
                                </p>
                                <p></p>
                                <div><img
                                    alt="contingency-matrix"
                                    src={matrix}
                                /></div>
                                <p></p>
                                <p>
                                    Above, we show the hypothetical matrix for gene 'Gene X' and MeSH term 'MeSH Y'.
                                    The publication count '(a)' describes the number of publications that mention both the
                                    gene and the MeSH term for the pairwise test, and this is stored in our database
                                    and returned with every search.
                                </p>
                                <p></p>
                                <p>We use the Chi Square test with one degree of freedom.</p>
                                <p></p>
                                <div><img
                                    alt="matrix-example"
                                    src={matrixExample}
                                /></div>
                                <p></p>
                                <p>
                                    Our implementation and the resulting value is validated by using these input
                                values as input to the Chi Square test implemented in R, using the same degrees of
                                    freedom value of one. The result from this test yields the same p-value as is
                                    returned by Winnow when these input terms are queried.
                                </p>
                                <div><img
                                    alt="rExample"
                                    src={rExample}
                                /></div>
                            </div>
                        </Collapse>
                    </div>
                    <div>
                        <hr/>
                    </div>
                    <div>
                        <Button
                            onClick={() => setOpen4(!open4)}
                            aria-controls="example-collapse-text"
                            aria-expanded={open4}
                            className="faq-button"
                        >
                            What types of browsers are supported on Winnow?
                        </Button>
                        <p></p>
                        <Collapse in={open4}>
                            <div>
                                <Table>
                                    <thead>
                                        <tr>
                                            <th>Browser</th>
                                            <th>Version</th>
                                            <th>Supported</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>Chrome</td>
                                            <td>&ge;48</td>
                                            <td>Yes</td>
                                        </tr>
                                        <tr>
                                            <td>Edge</td>
                                            <td>&ge;44</td>
                                            <td>Yes</td>
                                        </tr>
                                        <tr>
                                            <td>Firefox</td>
                                            <td>&ge;72</td>
                                            <td>Yes</td>
                                        </tr>
                                        <tr>
                                            <td>Internet Explorer</td>
                                            <td>Any</td>
                                            <td>No</td>
                                        </tr>
                                        <tr>
                                            <td>Opera</td>
                                            <td>&ge;68</td>
                                            <td>Yes</td>
                                        </tr>
                                        <tr>
                                            <td>Safari</td>
                                            <td>&ge;13</td>
                                            <td>Yes</td>
                                        </tr>
                                    </tbody>
                                </Table>
                            </div>
                        </Collapse>
                    </div>
                </Card.Body>
            </Card>
        </div>
    );
}

export default Support;