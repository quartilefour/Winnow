import React from 'react';
import * as AuthContext from "../../../context/auth";
import Login from '../../../components/user/Login';
import {mountWrap, shallowWrap} from "../../_helpers";
import {shallow} from "enzyme";


describe('<Login {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const wrappedShallow = () => shallowWrap(<Login {...props} />);
    const wrappedMount = () => mountWrap(<Login {...props} />);

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            location: { state: undefined},
            userEmail: 'jonny@harvard.edu',
            userPassword: 'Test1234!'
        };
        if (component) component.unmount();

       mockUseEffect();
       wrapper = shallow(<Login {...props} />);
       //wrapper = wrappedShallow();
    })

    test('should render with mock data in snapshot', () => {
        //const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
    });

    it('should render with defined referer', () => {
        props.location.state = { referer: "/profile"}
        mockUseEffect();
        wrapper = shallow(<Login {...props} />);
        expect(wrapper).toMatchSnapshot();
    })

    it('should have an email field', () => {
        expect(wrapper.find('FormControl[type="email"]').length).toEqual(1);
    });

    it('should have proper props for email field', () => {
        expect(wrapper.find('FormControl[type="email"]').props()).toEqual({
            'aria-placeholder': 'E-mail Address',
            autoComplete: "username",
            className: "form-control",
            id: "userEmail",
            name: "userEmail",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'E-mail Address',
            type: 'email',
            value: ''
        });
    });

    it('should have a password field', () => {
        expect(wrapper.find('FormControl[type="password"]').length).toEqual(1);
    });

    it('should have proper props for password field', () => {
        expect(wrapper.find('FormControl[type="password"]').props()).toEqual({
            'aria-placeholder': 'Password',
            autoComplete: "current-password",
            className: "form-control",
            id: "userPassword",
            name: "userPassword",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'Password',
            type: 'password',
            value: ''
        });
    });
    it('should have a submit button', () => {
        expect(wrapper.find('Button').length).toEqual(1);
    });

    it('should submit credentials when button is clicked', () => {
        const loginButton = wrapper.find('Button');
        loginButton.simulate('click');

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
    it('it should redirect after successful log in', () => {
        const contextValues = {
            authToken: 'jwttoken',
            setAuthToken: (e) => {this.authToken = e}
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        mockUseEffect();
        const wrapper = shallow(<Login {...props} />);
        expect(props.location.state).toEqual(undefined);
        expect(wrapper.find('Redirect').length).toEqual(1);
    })
});

