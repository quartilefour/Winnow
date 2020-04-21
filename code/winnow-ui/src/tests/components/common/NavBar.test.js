import React from 'react';
import { mount, shallow } from 'enzyme';
import NavBar from '../../../components/common/NavBar';

test('should test NavBar component', () => {
    const wrapper = shallow(<NavBar />);
    expect(wrapper).toMatchSnapshot();
});

describe('<NavBar />', () => {
    const intialProps =  {
        authToken: 'jwttoken'
    };
    const container = shallow(<NavBar {...intialProps} />);
        it('should have nav bar', () => {
            container.authToken = 'jwttoken';
            expect(container.find('Navbar').length).toEqual(1);
        });
});

