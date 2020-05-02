import React from 'react';
import {mount, shallow} from 'enzyme';
import RecentSearchesTab from '../../../components/user/RecentSearchesTab';
import * as ss from "../../../service/SearchService";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";


describe('<RecentSearchesTab {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const searchHistory = [
        {
            "searchQuery": {
                "geneId": ["285550"],
                "symbol": [],
                "description": [],
                "meshId": [],
                "meshTreeId": [],
                "name": []
            }
        }
    ]

    const response = {
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
    };

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }


    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");

        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<RecentSearchesTab {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should show RecentSearches Table', () => {
        expect(wrapper.find('div').length).toEqual(1);
    });

    it('loads', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, response)
        ss.getSearchHistory = jest.fn().mockReturnValue(searchHistory)
        const c = mount(<RecentSearchesTab {...props} />)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            mockUseEffect()
            const exeSearch = c.find('FontAwesomeIcon[title="Execute Search"]')
            expect(exeSearch.length).toEqual(1);
            exeSearch.simulate('click')
            console.log(`RST: ${c.debug()}`)
        });
    })

    /*
        it('should render when no error is passed', () => {
            props.error = null;
            mockUseEffect();
            wrapper = shallow(<RecentSearchesTab {...props} />);
            expect(props.error).toEqual(null);
            expect(wrapper.find('Card').length).toEqual(1);
        })
     */
});
