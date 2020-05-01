import React from 'react';
import {shallow} from 'enzyme';
import Error from '../../routes/Error';

describe('<Error {props}/>', () => {
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
            error: '404: Page Not Found!'
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<Error {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should show error message', () => {
        expect(wrapper.find('Card').length).toEqual(1);
    });

    it('loads error', () => {
        expect(props.error).toEqual('404: Page Not Found!');
    })

    it('should render when no error is passed', () => {
        props.error = null;
        mockUseEffect();
        wrapper = shallow(<Error {...props} />);
        expect(props.error).toEqual(null);
        expect(wrapper.find('Card').length).toEqual(1);
    })
});