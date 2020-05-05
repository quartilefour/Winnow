import React from 'react';
import {mount, shallow} from 'enzyme';
import RecentSearchesTab from '../../../components/user/RecentSearchesTab';
import * as ss from "../../../service/SearchService";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import {searchHistory, searchResults} from "../../data/mockResponseData";


describe('<RecentSearchesTab {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

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

    it('loads and executes search', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, searchResults)
        ss.getSearchHistory = jest.fn().mockReturnValue(searchHistory)
        const c = mount(<RecentSearchesTab {...props} />)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            mockUseEffect()
            const exeSearch = c.find('FontAwesomeIcon[title="Execute Search"]')
            expect(exeSearch.length).toEqual(2);
            exeSearch.at(0).simulate('click')
            //console.log(`RST: ${c.debug()}`)
        });
    });

    it('loads and removes search', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, searchResults)
        ss.getSearchHistory = jest.fn().mockReturnValue(searchHistory)
        ss.removeSearchHistory = jest.fn(0)
        const c = mount(<RecentSearchesTab {...props} />)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        mockUseEffect()
        const deleteSearch = c.find('FontAwesomeIcon[title="Delete Search"]')
        expect(deleteSearch.length).toEqual(2);
        deleteSearch.at(0).simulate('click')
        //console.log(`RST: ${c.debug()}`)
    });

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
