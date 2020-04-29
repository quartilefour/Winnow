import React from 'react';
import {mount, shallow} from 'enzyme';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import ComboSearchTab from '../../../components/common/ComboSearchTab';

describe('<ComboSearchTab />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const selectData =[
        {
            "geneId":"100188774",
            "description":"Deafness, cataract, retinitis pigmentosa, and sperm abnormalities",
            "symbol":"DFCTRPS"
        },
        {
            "geneId":"100189019",
            "description":"tRNA-Pro (anticodon AGG) 2-1",
            "symbol":"TRP-AGG2-1"
        },
        {
            "geneId":"100189020",
            "description":"tRNA-Pro (anticodon AGG) 1-1",
            "symbol":"TRP-AGG1-1"
        }
    ]

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
        geneSelect.simulate('inputChange', [{keyCode: 84},{keyCode: 82}])

        geneSelect.simulate('change', null)
        geneSelect.simulate('change', [{ label: 'TRP-AGG1-1', value: "100189020"}])
        //console.log(wrapper.debug())
    });

    it('should have results after clicking search', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(new RegExp(`${WINNOW_API_BASE_URL}/genes/search/*`))
            .reply(200, selectData)
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, selectData)
        const button = wrapper.find('Button');
        expect(button.length).toEqual(2);

        //console.log(wrapper.debug())
        const geneSelect = wrapper.find('StateManager');
        expect(geneSelect.length).toEqual(1);

        geneSelect.simulate('inputChange', [{keyCode: 84},{keyCode: 82}])
        geneSelect.simulate('change', [{ label: 'TRP-AGG1-1', value: "100189020"}])
        button.last().simulate('click');

        //mockUseEffect();
        /*props.hasResults = true;
        const c = mount(<ComboSearchTab {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        console.log(c.debug())*/
        //expect(wrapper.find('SearchTermUploader').length).toEqual(1)
    });

    it('should have file and textarea input after selecting batch mode', () => {
        const button = wrapper.find('Button');
        expect(button.length).toEqual(2);

        button.first().simulate('click');
        expect(wrapper.find('SearchTermUploader').length).toEqual(1)
    });


});