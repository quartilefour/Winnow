import React from 'react';
import { shallow } from 'enzyme';
import RecentSearchesTab from '../../../components/user/RecentSearchesTab';

test('should test RecentSearchesTab component', () => {
    const wrapper = shallow(<RecentSearchesTab />);
    expect(wrapper).toMatchSnapshot();
});