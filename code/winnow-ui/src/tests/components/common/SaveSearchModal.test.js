import React from 'react';
import {mount, shallow} from 'enzyme';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import SaveSearchModal from '../../../components/common/SaveSearchModal';
import {mountWrap, shallowWrap} from "../../_helpers";

describe('<SaveSearchModal />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;


    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    const wrappedShallow = () => shallowWrap(<SaveSearchModal {...props} />);
    const wrappedMount = () => mountWrap(<SaveSearchModal {...props} />);

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            searchdata: {
                "searchQuery": {
                    "geneId": ["7168", "7169", "7170", "7171"],
                    "symbol": [],
                    "description": [],
                    "meshId": [],
                    "meshTreeId": ["C06.689.202"],
                    "name": []
                },
                "results": [{
                    "index": 0,
                    "geneId": "7171",
                    "description": "tropomyosin 4",
                    "symbol": "TPM4",
                    "meshId": "D003550",
                    "name": "Cystic Fibrosis",
                    "publicationCount": 1,
                    "pvalue": 0.05446439662
                }, {
                    "index": 1,
                    "geneId": "7169",
                    "description": "tropomyosin 2",
                    "symbol": "TPM2",
                    "meshId": "D003550",
                    "name": "Cystic Fibrosis",
                    "publicationCount": 1,
                    "pvalue": 0.189968535432
                }, {
                    "index": 2,
                    "geneId": "7170",
                    "description": "tropomyosin 3",
                    "symbol": "TPM3",
                    "meshId": "D003550",
                    "name": "Cystic Fibrosis",
                    "publicationCount": 1,
                    "pvalue": 0.343838274335
                }, {
                    "index": 3,
                    "geneId": "7168",
                    "description": "tropomyosin 1",
                    "symbol": "TPM1",
                    "meshId": "D003550",
                    "name": "Cystic Fibrosis",
                    "publicationCount": 1,
                    "pvalue": 0.581850965367
                }]
            },
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<SaveSearchModal {...props}/>);
    })

    it('should render with mock data in snapshot', () => {
        //const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
    });

    it('should get some mock data', async () => {
        const mockfBM = new MockAdapter(axios);
        mockfBM
            .onPost(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, "Saved bookmark");
        const c = mount(<SaveSearchModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
    });

    it('should have input for bookmark', () => {
        expect(wrapper.find('FormControl[id="bm-input"]').length).toEqual(1);
    });
    /*

    it('should have proper props for progress bar', () => {
        expect(container.find('ProgressBar')).toHaveProp({
            animated: true,
            label: 'Loading...',
        });
    });
     */
});