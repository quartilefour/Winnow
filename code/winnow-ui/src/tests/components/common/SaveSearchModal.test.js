import React from 'react';
import {mount, shallow} from 'enzyme';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import SaveSearchModal from '../../../components/common/SaveSearchModal';
import {searchResults} from "../../data/mockResponseData";

describe('<SaveSearchModal />', () => {
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
            show: true,
            searchdata: searchResults,
            onHide: jest.fn()
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<SaveSearchModal {...props}/>);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should get some mock data and save new bookmark', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, "Saved bookmark");
        const c = mount(<SaveSearchModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
        const bmInput = c.find('FormControl[id="bm-input"]');
        expect(bmInput.length).toEqual(1);
        bmInput.simulate('change', {
            persist: () => {
            }, target: {name: 'bm-input', value: 'BM 1'}
        });
        const saveButton = c.find('Button').last();
        saveButton.simulate('click')
    });

    it('should have input for bookmark and save on enter', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, "Saved bookmark");
        const c = mount(<SaveSearchModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
        const bmInput = c.find('FormControl[name="bm-input"]');
        expect(bmInput.length).toEqual(1);
        bmInput.simulate('change', {
            persist: () => {
            }, target: {name: 'bm-input', value: 'BM 1'}
        });
        mockUseEffect()
        mockUseEffect()
        bmInput.simulate('keypress', {key: 'Enter'});
    });
    /*

    it('should have proper props for progress bar', () => {
        expect(container.find('ProgressBar')).toHaveProp({
            animated: true,
            label: 'Loading...',
        });
    });
     */
});