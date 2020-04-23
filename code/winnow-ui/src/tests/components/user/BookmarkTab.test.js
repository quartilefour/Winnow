import React from 'react';
import { shallow } from 'enzyme';
import BookmarkTab from '../../../components/user/BookmarkTab';

describe('<BookmarkTab {props}/>', () => {
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
        wrapper = shallow(<BookmarkTab {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have PageLoader', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });
});
