import React from 'react';
import { mount, shallow } from 'enzyme';
import RecentSearchesTab from '../../../components/user/RecentSearchesTab';
import {getSearchHistory} from "../../../service/SearchService";


describe('<RecentSearchesTab {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const searchHistory = [
        {
            "index": 0,
            "searchQuery":{"geneId":["285550"],"symbol":[],"description":[],"meshId":[],"meshTreeId":[],"name":[]}
        }
    ]

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

    it('loads', () => {
        const c = mount(<RecentSearchesTab {...props} />)
        c.update()
        jest.mock(getSearchHistory(), () => {return [{
            "searchQuery":{"geneId":["285550"],"symbol":[],"description":[],"meshId":[],"meshTreeId":[],"name":[]}
        }]})
        mockUseEffect()
        console.log(`RST: ${c.debug()}`)
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
