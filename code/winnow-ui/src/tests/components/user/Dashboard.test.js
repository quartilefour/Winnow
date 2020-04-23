import React from 'react';
import { shallow } from 'enzyme';
import Dashboard from '../../../components/user/Dashboard';

test('should test Dashboard component', () => {
    const wrapper = shallow(<Dashboard />);
    expect(wrapper).toMatchSnapshot();
});