import React from 'react';
import {shallow} from 'enzyme';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../../constants";
import SearchResultsDisplay from '../../../components/common/SearchResultsDisplay';

describe('<SearchResultsDisplay />', () => {

    let props;
    let wrapper;
    let useEffect;
    let component;

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
        props = {
            resData: response
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<SearchResultsDisplay {...props}/>);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
        //console.log(wrapper.debug());
    });

    /*
    it('should have progress bar', () => {
        expect(container.find('ProgressBar').length).toEqual(1);
    });

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