import React from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import * as AuthContext from "../../context/auth";
import ForgotPassword from '../../routes/ForgotPassword';
import {mount, shallow} from "enzyme";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../constants";
import {act} from "react-dom/test-utils";


describe('<ForgotPassword {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            userEmail: 'jonny@harvard.edu',
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<ForgotPassword {...props} />);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

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

    it('should have a submit button', () => {
        expect(wrapper.find('Button').length).toEqual(1);
    });

    it('should submit credentials when button is clicked', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/forgot`)
            .reply(200);
        const c = mount(<Router><ForgotPassword {...props}/></Router>)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        c.find('FormControl[name="userEmail"]').simulate('change', {
            persist: () => {
            },
            target: {
                name: 'userEmail',
                value: 'jonny@harvard.edu'
            }
        })
        expect(c.find('FormControl[name="userEmail"]').props().value).toEqual('jonny@harvard.edu')
        c.find('Form').simulate('submit')

    });

    it('it should redirect after successful log in', () => {
        const contextValues = {
            authToken: 'jwttoken'
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        mockUseEffect();
        const wrapper = shallow(<ForgotPassword {...props} />);
        expect(wrapper.find('Redirect').length).toEqual(1);
    })
});

