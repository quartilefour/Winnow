import React from 'react';
import {shallow} from 'enzyme';
import Dashboard from '../../routes/Dashboard';

describe('<Dashboard {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<Dashboard {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have Tab Container', () => {
        expect(wrapper.find('TabContainer').length).toEqual(1);
    });

    it('should have be able to select Tab', () => {
        const tabs = wrapper.find('NavLink');
        expect(tabs.length).toEqual(3);
        tabs.at(1).simulate('click')

        const container = wrapper.find('TabContainer')
        container.simulate('select', {target: {value: 'search'}})
        //console.log(wrapper.debug());
    });

    /*
        it('loads error', () => {
            expect(props.error).toEqual('404: Page Not Found!');
        })

        it('should render when no error is passed', () => {
            props.error = null;
            mockUseEffect();
            wrapper = shallow(<Dashboard {...props} />);
            expect(props.error).toEqual(null);
            expect(wrapper.find('Card').length).toEqual(1);
        })
     */
});
