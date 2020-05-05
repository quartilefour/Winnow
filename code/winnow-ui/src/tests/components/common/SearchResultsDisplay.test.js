import React from 'react';
import {shallow} from 'enzyme';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../../constants";
import SearchResultsDisplay from '../../../components/common/SearchResultsDisplay';
import {searchResults} from "../../data/mockResponseData";

describe('<SearchResultsDisplay />', () => {

    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            history: jest.fn(() => {
            }),
            resultData: searchResults
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

     */
});