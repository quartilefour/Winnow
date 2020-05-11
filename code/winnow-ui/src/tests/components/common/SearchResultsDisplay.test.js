import React from 'react';
import {mount, shallow} from 'enzyme';
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
    });

    it('should get some mock data', () => {
        const c = mount(<SearchResultsDisplay {...props}/>);
        console.info(`SearchResultsDisplay: ${c.debug()}`)

        const csvExport = c.find('ExportCSV')
        expect(csvExport.length).toEqual(1);

        const btnGene = c.find('.btn-gene')
        expect(btnGene.length).toEqual(8)

        btnGene.at(0).simulate('click')

        const btnPub = c.find('Button[title~="Publication"]')
        expect(btnPub.length).toEqual(4)

        btnPub.at(0).simulate('click')

    });

});