import React from 'react';
import { shallow } from 'enzyme';
import App from "../App";

describe('<App />', () => {

    let props;
    let wrapper;
    let useEffect;
    let component;

    const { location } = window;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        delete window.location;
        window.location = {
            host: 'localhost:3000'
        }
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<App {...props} />);
    });

    afterAll(() => {
        window.location = location;
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have Maintenance component is API unavailable', () => {
        expect(wrapper.find('Maintenance').length).toEqual(1);
    });
});