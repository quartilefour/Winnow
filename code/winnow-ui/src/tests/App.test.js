import React from 'react';
import {mount, shallow} from 'enzyme';
import App from "../App";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL, WINNOW_TOKEN} from "../constants";
import {act} from "react-dom/test-utils";

describe('<App />', () => {

    let props;
    let wrapper;
    let useEffect;
    let component;

    const { location } = window;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        delete window.location;
        window.location = {
            host: 'localhost:3000'
        }
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<App {...props} />);
    });

    afterAll(() => {
        window.location = location;
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

   /* it('should have Maintenance component is API unavailable', async () => {
        jest.setTimeout(30000);
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/status`)
            .reply(400, "API unavailable");
        const c = mount(<App {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        console.log(c.debug());
        expect(c.find('Maintenance').length).toEqual(1);
    }); */

    it('should get some mock data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/status`)
            .reply(200, "API available");
        const c = mount(<App {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        expect(c.find('NavBar').length).toEqual(1);
        //console.log(c.debug());
    });

    it('should get have cookie', async () => {
        Object.defineProperty(window.document, 'cookie', {
            writable: true,
            value: `${WINNOW_TOKEN}=jwttoken`,
        });
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/status`)
            .reply(200, "API available");
        const c = mount(<App {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //expect(c.find('NavBar').length).toEqual(1);
        //console.log(c.debug());
    });
});