import React from 'react';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../constants";
import Profile from '../../routes/Profile';
import {mountWrap, shallowWrap} from "../_helpers";
import {mount, shallow} from "enzyme";

describe('<Profile />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const response = {"userEmail": "jonny@harvard.edu", "firstName": "John", "lastName": "Harvard"};

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
        wrapper = shallow(<Profile/>);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have page loader is not loaded', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    it('should submit mock profile data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/profile`)
            .reply(200, response)
            .onPatch(`${WINNOW_API_BASE_URL}/profile`)
            .reply(200, {})
        const c = mount(<Profile/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            c.find('Form').first().simulate('submit')
        });
        //console.log(c.debug());
    });

    it('should get mock password data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/profile`)
            .reply(200, response)
            .onPatch(`${WINNOW_API_BASE_URL}/profile`)
            .reply(200, {})
        const c = mount(<Profile/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            c.find('Form').last().simulate('submit')
        });
        //console.log(c.debug());
    });

    it('should get mock profile error', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/profile`)
            .reply(500, "Internal server error");
        const c = mount(<Profile/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
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

