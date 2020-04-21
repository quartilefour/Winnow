import React from 'react';
import { shallow } from 'enzyme';
import Error from '../../../components/error/Error';

test('should test Error component', () => {
    const wrapper = shallow(<Error />);
    expect(wrapper).toMatchSnapshot();
});

describe('<Error />', () => {
    const container = shallow(<Error />);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
    });

    it('should have progress bar', () => {
        expect(container.find('Card').length).toEqual(1);
    });
});