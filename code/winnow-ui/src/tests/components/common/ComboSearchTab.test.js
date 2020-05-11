import React from 'react';
import {mount, shallow} from 'enzyme';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import ComboSearchTab from '../../../components/common/ComboSearchTab';
import {responseTree, searchResults, selectData} from "../../data/mockResponseData";

describe('<ComboSearchTab />', () => {
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

        mockUseEffect();
        wrapper = shallow(<ComboSearchTab {...props}/>);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have select input', () => {
        //console.log(wrapper.debug())
        const geneSelect = wrapper.find('StateManager');
        expect(geneSelect.length).toEqual(1);
    });

    it('should have select options', () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(new RegExp(`${WINNOW_API_BASE_URL}/genes/search/*`))
            .reply(200, selectData)
        //console.log(wrapper.debug())
        const geneSelect = wrapper.find('StateManager');
        expect(geneSelect.length).toEqual(1);

        geneSelect.simulate('inputChange', {keyCode: 80})
        geneSelect.simulate('inputChange', [{keyCode: 84}, {keyCode: 82}])

        geneSelect.simulate('change', null)
        geneSelect.simulate('change', [{label: 'TRP-AGG1-1', value: "100189020"}])
        //console.log(wrapper.debug())
    });

    it('should have results after clicking search', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(new RegExp(`${WINNOW_API_BASE_URL}/genes/search/*`))
            .reply(200, selectData)
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, searchResults)
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/tree`)
            .reply(200, responseTree);
        const c = mount(<ComboSearchTab {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            const button = wrapper.find('Button');
            expect(button.length).toEqual(2);

            //console.log(wrapper.debug())
            const geneSelect = wrapper.find('StateManager');
            expect(geneSelect.length).toEqual(1);

            geneSelect.simulate('inputChange', [{keyCode: 84}, {keyCode: 82}])
            geneSelect.simulate('change', [{label: 'TRP-AGG1-1', value: "100189020"}])
            button.at(0).simulate('click');
            mockUseEffect()
            c.update()
        });

    });

    it('should have file and textarea input after selecting batch mode', () => {
        const button = wrapper.find('Button');
        expect(button.length).toEqual(2);

        console.log(wrapper.debug())

        button.at(1).simulate('click');
        expect(wrapper.find('SearchTermUploader').length).toEqual(1)
    });


});