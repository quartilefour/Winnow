import React from 'react';
import NavBar from '../../../components/common/NavBar';
import {mountWrap, shallowWrap} from "../../_helpers";
import {mount, shallow} from "enzyme";
import * as AuthContext from "../../../context/auth";
import ReactDOM from "react-dom";
import {act} from "react-dom/test-utils";
import BookmarkTab from "../../../components/user/BookmarkTab";

describe('<NavBar />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const response = {"userEmail": "jonny@harvard.edu", "firstName": "John", "lastName": "Harvard"};

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<NavBar/>);
    });

    it ('it should have empty Navbar if not logged in', () => {
        const w = shallow(<NavBar />);
        expect(w.find('Navbar').length).toEqual(1);
        expect(w.find('NavbarBrand').length).toEqual(0);
    })

    it('it should have Navbar Brand if logged in', () => {
        const contextValues = {
            authToken: 'jwttoken',
            setAuthToken: (e) => {this.authToken = e}
        }
        jest.spyOn(AuthContext, 'useAuth')
            .mockImplementation(() => contextValues);
        const w = shallow(<NavBar />);
        expect(w.find('NavbarBrand').length).toEqual(1);
    })

    it('it should call logout', () => {
       const contextValues = {
           authToken: 'jwttoken',
           setAuthToken: (e) => {this.authToken = e}
       }
       jest.spyOn(AuthContext, 'useAuth')
           .mockImplementation(() => contextValues);
       const w = shallow(<NavBar />);
       const logoutLink = w.find('NavLink[href="#"]');
       expect(logoutLink.length).toEqual(1);
       logoutLink.simulate('click');
       // expect(w.find('NavbarBrand').length).toEqual(0);
    })
});

