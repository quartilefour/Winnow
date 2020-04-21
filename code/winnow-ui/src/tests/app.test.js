import React from 'react';
import { shallow } from 'enzyme';
import App from "../App";

test('should test App component', () => {
    const wrapper = shallow(<App />);
    expect(wrapper).toMatchSnapshot();
});

describe('<App />', () => {
    const { location } = window;
    beforeEach(() => {
        delete window.location;
        window.location = {
            host: 'localhost:3000'
        }
    });
    afterAll(() => {
        window.location = location;
    });

    const container = shallow(<App />);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
    });

    it('should have Maintenance component is API unavailable', () => {
        expect(container.find('Maintenance').length).toEqual(1);
    });
});