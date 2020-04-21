import React from 'react';
import { shallow } from 'enzyme';
import Support from '../../../components/user/Support';

test('should test Support component', () => {
    const wrapper = shallow(<Support />);
    expect(wrapper).toMatchSnapshot();
});