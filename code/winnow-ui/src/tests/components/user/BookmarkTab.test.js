import React from 'react';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {mount, shallow} from 'enzyme';
import {act} from "react-dom/test-utils";
import BookmarkTab from '../../../components/user/BookmarkTab';
import {WINNOW_API_BASE_URL} from "../../../constants";
import {bookmarkData, searchResults} from "../../data/mockResponseData";

describe('<BookmarkTab {props}/>', () => {
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
        wrapper = shallow(<BookmarkTab {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have PageLoader', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    it('should get some mock data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, bookmarkData);
        const c = mount(<BookmarkTab/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
    })

    it('should execute a bookmark', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, bookmarkData)
            .onPost(`${WINNOW_API_BASE_URL}/search`)
            .reply(200, searchResults);
        const c = mount(<BookmarkTab/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update();

            const executeSearch = c.find('FontAwesomeIcon[title="Execute Search"]').last()
            executeSearch.simulate('click')
        });
        //console.log(c.debug());
    })

    it('should delete a bookmark', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, bookmarkData)
            .onDelete(new RegExp(`${WINNOW_API_BASE_URL}/bookmarks/\\d.*`))
            .reply(200);
        const c = mount(<BookmarkTab/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update();

            const deleteSearch = c.find('FontAwesomeIcon[title="Delete Search"]').first()
            deleteSearch.simulate('click')
        });
        //console.log(c.debug());
    })
});
