import React from 'react';
import Profile from '../../../components/user/Profile';
import {mountWrap, shallowWrap} from "../../_helpers";
import {shallow} from "enzyme";
import Login from "../../../components/user/Login";


describe('<Profile />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    const wrappedShallow = () => shallowWrap(<Profile {...props} />);
    const wrappedMount = () => mountWrap(<Profile {...props} />);

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<Profile />);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have page loader is not loaded', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    /*
    it('should have an email field', () => {
        const wrapper = wrappedShallow();
        expect(wrapper.find('FormControl[type="email"]').length).toEqual(1);
    });

    it('should have proper props for email field', () => {
        const wrapper = wrappedShallow();
        expect(wrapper.find('FormControl[type="email"]').props()).toEqual({
            autoComplete: "username",
            name: "userEmail",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'E-mail Address',
            type: 'email',
            value: ''
        });
    });

    it('should have a password field', () => {
        const wrapper = wrappedShallow();
        expect(wrapper.find('FormControl[type="password"]').length).toEqual(2);
    });

    it('should have proper props for password field', () => {
        const wrapper = wrappedShallow();
        expect(wrapper.find('FormControl[type="password"]').first().props()).toEqual({
            autoComplete: "new-password",
            name: "userPassword",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'Password',
            type: 'password',
            value: ''
        });
    });
    it('should have a submit button', () => {
        const wrapper = wrappedShallow();
        expect(wrapper.find('Button').length).toEqual(1);
    });

    it('should submit credentials when button is clicked', () => {
        const wrapper = wrappedShallow();
        const loginButton = wrapper.find('Button');
        loginButton.simulate('click');
        //expect(wrapper.find('Button').length).toEqual(1);

    });

    /*
    it('button click changes submits form', async () => {
        // cache button element
        const button = container.find('Button').last();
        const eventMock = {
            target: {
                onClick: function () {
                    return true
                }
            }
        };
        // pass mocked event object
        //await button.onSubmit(eventMock);
        expect(container.find('Button').length).toEqual(1);
    });
    */
});

