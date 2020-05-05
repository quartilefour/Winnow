import React from 'react';
import {shallow} from 'enzyme';
import Support from '../../routes/Support';

describe('<Support />', () => {

    let wrapper;
    let component;

    beforeEach(() => {
        if (component) component.unmount();
        wrapper = shallow(<Support/>);
    })

    it('should test Support component', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have # buttons corresponding to the # of topics', () => {
        const numOfButtons = 4;
        const buttons = wrapper.find('Button');
        expect(buttons.length).toEqual(numOfButtons);

        buttons.forEach((b) => {
            b.simulate('click')
        })
    });

});