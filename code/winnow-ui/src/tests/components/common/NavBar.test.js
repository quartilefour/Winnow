import React, {useEffect} from 'react';
import NavBar from '../../../components/common/NavBar';
import {mountWrap, shallowWrap} from "../../_helpers";
import {mount, shallow} from "enzyme";
import * as AuthContext from "../../../context/auth";
import ReactDOM from "react-dom";
import {act} from "react-dom/test-utils";

const mockUseEffect = () => {
    useEffect.mockImplementationOnce(f => f());
};

describe('<NavBar />', () => {
    it ('it should have empty Navbar if not logged in', () => {
        const wrapper = shallow(<NavBar />);
        expect(wrapper.find('Navbar').length).toEqual(1);
        expect(wrapper.find('NavbarBrand').length).toEqual(0);
    })

    it('it should have Navbar Brand if logged in', () => {
        const contextValues = {
            authToken: 'jwttoken',
            setAuthToken: (e) => {this.authToken = e}
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        const wrapper = shallow(<NavBar />);
        expect(wrapper.find('NavbarBrand').length).toEqual(1);
    })

    it('it should call logout', () => {
       const contextValues = {
           authToken: 'jwttoken',
           setAuthToken: (e) => {this.authToken = e}
       }
       jest.spyOn(AuthContext, 'useAuth')
           .mockImplementation(() => contextValues);
       const wrapper = shallow(<NavBar />);
       const logoutLink = wrapper.find('NavLink[href="#"]');
       expect(logoutLink.length).toEqual(1);
       logoutLink.simulate('click');
       // expect(wrapper.find('NavbarBrand').length).toEqual(0);
    })
});

