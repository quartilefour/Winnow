import React from 'react';
import { shallow } from 'enzyme';
import Maintenance from '../../../components/error/Maintenance';

test('should test Maintenance component', () => {
    const wrapper = shallow(<Maintenance />);
    expect(wrapper).toMatchSnapshot();
});

describe('<Maintenance />', () => {
    const container = shallow(<Maintenance />);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
    });

    it('should have progress bar', () => {
        expect(container.find('Card').length).toEqual(1);
    });
});