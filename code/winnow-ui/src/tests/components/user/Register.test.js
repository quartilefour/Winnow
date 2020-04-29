import React from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import Register from '../../../components/user/Register';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {mountWrap, shallowWrap} from "../../_helpers";
import {mount} from "enzyme";


describe('<Register />', () => {
    let props;
    let component;
    const wrappedShallow = () => shallowWrap(<Register {...props} />);
    const wrappedMount = () => mountWrap(<Register {...props} />);
    beforeEach(() => {
        props = {
            location: { state: undefined},
            userEmail: 'jonny@harvard.edu',
            userPassword: 'Test1234!'
        };
        if (component) component.unmount();
    })

    test('should render with mock data in snapshot', () => {
        const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
    });

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

    it('should get mock profile data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/registration`)
            .reply(201, "Account created");
        const c = mount(<Router><Register/></Router>)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
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

