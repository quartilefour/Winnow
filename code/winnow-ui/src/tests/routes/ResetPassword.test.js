import React from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import * as AuthContext from "../../context/auth";
import ResetPassword from '../../routes/ResetPassword';
import {mount, shallow} from "enzyme";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../constants";
import {act} from "react-dom/test-utils";

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useLocation: () => ({
        search: "?token=mysecrettoken"
    })
}));

describe('<ResetPassword />', () => {
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

        wrapper = shallow(<Router><ResetPassword/></Router>);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have a password field', () => {
        const c = mount(<Router><ResetPassword/></Router>)
        expect(c.find('FormControl[type="password"]').length).toEqual(2);
    });

    it('should have proper props for password field', () => {
        const c = mount(<Router><ResetPassword/></Router>)
        expect(c.find('FormControl[type="password"]').first().props()).toEqual({
            autoComplete: "new-password",
            name: "userPasswordNew",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'New Password',
            type: 'password',
            value: ''
        });
    });

    it('should have a submit button', () => {
        const c = mount(<Router><ResetPassword/></Router>)
        expect(c.find('Button').length).toEqual(1);
    });

    it('should submit credentials when button is clicked', async () => {

        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/reset`)
            .reply(200);
        const c = mount(<Router><ResetPassword/></Router>)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            //console.log(c.debug())
            c.find('FormControl[name="userPasswordNew"]').simulate('change', {
                target: {
                    name: 'userPasswordNew',
                    value: 'Test1234!'
                }
            })
            c.find('FormControl[name="passwordConfirm"]').simulate('change', {
                target: {
                    name: 'passwordConfirm',
                    value: 'Test1234!'
                }
            })
            c.find('Form').simulate('submit')
        });
    });

    it('it should redirect after successful log in', () => {
        const contextValues = {
            authToken: 'jwttoken'
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        const c = mount(<Router><ResetPassword/></Router>);
        c.update()
        expect(c.find('Redirect').length).toEqual(1);
    })
});

