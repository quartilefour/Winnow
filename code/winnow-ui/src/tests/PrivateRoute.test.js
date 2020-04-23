import React from 'react';
import PrivateRoute from '../PrivateRoute';
import {mount, shallow} from "enzyme";
import * as AuthContext from "../context/auth";

describe('<PrivateRoute />', () => {
    let props;
    let wrapper;
    let component;

    beforeEach(() => {
        props = {
            component: React.Component, ...{}
        };
        if (component) component.unmount();

        wrapper = shallow(<PrivateRoute {...props} />);
    });



    it ('it should have Route', () => {
        expect(wrapper.find('Route').length).toEqual(1);
    })

    /*
    it('it should have Component if logged in', () => {
        const contextValues = {
            authToken: 'jwttoken',
            setAuthToken: (e) => {this.authToken = e}
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        wrapper = shallow(<PrivateRoute {...props}/>);
        expect(wrapper.find('Redirect').length).toEqual(1);
    })

     */
});

