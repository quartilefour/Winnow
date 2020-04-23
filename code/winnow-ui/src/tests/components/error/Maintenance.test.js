import React from 'react';
import {shallow} from 'enzyme';
import {mountWrap, shallowWrap} from "../../_helpers";
import Maintenance from '../../../components/error/Maintenance';

describe('<Maintenance {props}/>', () => {

    let props;
    let wrapper;
    let useEffect;
    let component;
    const wrappedShallow = () => shallowWrap(<Maintenance {...props} />);

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            error: 'API under going maintenance'
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<Maintenance {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should display downtime screen', () => {
        expect(wrapper.find('Card').length).toEqual(1);
    });

    it('should render when no error is passed', () => {
        props.error = null;
        mockUseEffect();
        wrapper = shallow(<Maintenance {...props} />);
        expect(props.error).toEqual(null);
        expect(wrapper.find('Card').length).toEqual(1);
    })
});