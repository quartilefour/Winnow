import React from 'react';
import { mount,shallow } from 'enzyme';
import {mountWrap, shallowWrap} from "../../_helpers";
import {MeshtermTree} from '../../../components/mesh/MeshtermTree';

test('should test MeshtermTree component', () => {
    const wrappedShallow = () => shallowWrap(<MeshtermTree />)
    const wrapper = wrappedShallow();
    expect(wrapper).toMatchSnapshot();
});

describe('<MeshtermTree {props}/>', () => {
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
            callback: jest.fn().mockResolvedValue([]),
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<MeshtermTree {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should show PageLoader', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });
});
