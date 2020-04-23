import React from 'react';
import { shallow } from 'enzyme';
import RecentSearchesTab from '../../../components/user/RecentSearchesTab';

test('should test RecentSearchesTab component', () => {
    const wrapper = shallow(<RecentSearchesTab />);
    expect(wrapper).toMatchSnapshot();
});

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

    it('should show RecentSearches Form', () => {
        expect(wrapper.find('Form').length).toEqual(1);
    });

/*
    it('loads error', () => {
        expect(props.error).toEqual('404: Page Not Found!');
    })

    it('should render when no error is passed', () => {
        props.error = null;
        mockUseEffect();
        wrapper = shallow(<RecentSearchesTab {...props} />);
        expect(props.error).toEqual(null);
        expect(wrapper.find('Card').length).toEqual(1);
    })
 */
});
