import React from 'react';
import {mount, shallow} from 'enzyme';
import GeneDetailModal from '../../../components/gene/GeneDetailModal';
import {mountWrap, shallowWrap} from "../../_helpers";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {NCBI_API_BASE, WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";

describe('<GeneDetailModal />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const response = {
        "geneId": "7169",
        "symbol": "TPM2",
        "description": "tropomyosin 2",
        "meshResults": [{
            "meshId": "D020107",
            "name": "Troponin T",
            "publicationCount": 3,
            "pvalue": 0.0
        }, {
            "meshId": "D019210",
            "name": "Troponin I",
            "publicationCount": 5,
            "pvalue": 0.0
        }, {
            "meshId": "D010947",
            "name": "Plants, Toxic",
            "publicationCount": 1,
            "pvalue": 0.0
        }],
        "geneResults": [{
            "geneId": "7168",
            "description": "tropomyosin 1",
            "symbol": "TPM1",
            "publicationCount": 39
        }, {
            "geneId": "7170",
            "description": "tropomyosin 3",
            "symbol": "TPM3",
            "publicationCount": 32
        }, {
            "geneId": "7171",
            "description": "tropomyosin 4",
            "symbol": "TPM4",
            "publicationCount": 22
        }]
    };

    const ncbiResponse = {
        "header": {
            "type": "esummary",
            "version": "0.3"
        },
        "result": {
            "uids": [
                "7171"
            ],
            "7171": {
                "uid": "7171",
                "name": "TPM4",
                "description": "tropomyosin 4",
                "status": "",
                "currentid": "",
                "chromosome": "19",
                "geneticsource": "genomic",
                "maplocation": "19p13.12-p13.11",
                "otheraliases": "HEL-S-108",
                "otherdesignations": "tropomyosin alpha-4 chain|TM30p1|epididymis secretory protein Li 108",
                "nomenclaturesymbol": "TPM4",
                "nomenclaturename": "tropomyosin 4",
                "nomenclaturestatus": "Official",
                "mim": [
                    "600317"
                ],
                "genomicinfo": [
                    {
                        "chrloc": "19",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004,
                        "exoncount": 15
                    }
                ],
                "geneweight": 4094,
                "summary": "This gene encodes a member of the tropomyosin family of actin-binding proteins involved in the contractile system of striated and smooth muscles and the cytoskeleton of non-muscle cells. Tropomyosins are dimers of coiled-coil proteins that polymerize end-to-end along the major groove in most actin filaments. They provide stability to the filaments and regulate access of other actin-binding proteins. In muscle cells, they regulate muscle contraction by controlling the binding of myosin heads to the actin filament. Multiple transcript variants encoding different isoforms have been found for this gene. [provided by RefSeq, Nov 2009]",
                "chrsort": "19",
                "chrstart": 16067506,
                "organism": {
                    "scientificname": "Homo sapiens",
                    "commonname": "human",
                    "taxid": 9606
                },
                "locationhist": [
                    {
                        "annotationrelease": "109.20200228",
                        "assemblyaccver": "GCF_000001405.39",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "109.20191205",
                        "assemblyaccver": "GCF_000001405.39",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "109.20190905",
                        "assemblyaccver": "GCF_000001405.39",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "109.20190607",
                        "assemblyaccver": "GCF_000001405.39",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "109",
                        "assemblyaccver": "GCF_000001405.38",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "108",
                        "assemblyaccver": "GCF_000001405.33",
                        "chraccver": "NC_000019.10",
                        "chrstart": 16067506,
                        "chrstop": 16103004
                    },
                    {
                        "annotationrelease": "108",
                        "assemblyaccver": "GCF_000306695.2",
                        "chraccver": "NC_018930.2",
                        "chrstart": 16177721,
                        "chrstop": 16213290
                    },
                    {
                        "annotationrelease": "105",
                        "assemblyaccver": "GCF_000001405.25",
                        "chraccver": "NC_000019.9",
                        "chrstart": 16178316,
                        "chrstop": 16213814
                    },
                    {
                        "annotationrelease": "105",
                        "assemblyaccver": "GCF_000002125.1",
                        "chraccver": "AC_000151.1",
                        "chrstart": 15749180,
                        "chrstop": 15784765
                    },
                    {
                        "annotationrelease": "105",
                        "assemblyaccver": "GCF_000306695.2",
                        "chraccver": "NC_018930.2",
                        "chrstart": 16177721,
                        "chrstop": 16213290
                    }
                ]
            }
        }
    };


    const wrappedShallow = () => shallowWrap(<GeneDetailModal {...props} />);
    const wrappedMount = () => mountWrap(<GeneDetailModal {...props} />);

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            active: true,
            geneid: 7169,
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<GeneDetailModal {...props} />);
    })

    it('should render with mock data in snapshot', () => {
        //const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
    });

    it('should have progress bar while loading', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    it('should get some mock data', async () => {
        const mockfBM = new MockAdapter(axios);
        mockfBM
            .onPost(`${WINNOW_API_BASE_URL}/genes`)
            .reply(200, response)
            .onGet(`${NCBI_API_BASE}/esummary.fcgi`)
            .reply(200, ncbiResponse);
        const c = mount(<GeneDetailModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
    });

    /*
    it('should have proper props for progress bar', () => {
        expect(container.find('ProgressBar')).toHaveProp({
            animated: true,
        });
        expect(container.find('ProgressBar')).toHaveProp({
            now: 100,
        });
        expect(container.find('ProgressBar')).toHaveProp({
            variant: "info",
        });
        expect(container.find('ProgressBar')).toHaveProp({
            label: 'Loading...',
        });
    });
     */
});