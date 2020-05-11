import React from 'react';
import {mount, shallow} from 'enzyme';
import GeneDetailModal from '../../../components/gene/GeneDetailModal';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {NCBI_API_BASE, WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import {geneResponse, ncbiResponse} from "../../data/mockResponseData";
import * as api from "../../../service/ApiService";

describe('<GeneDetailModal />', () => {
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
            active: 1,
            geneid: '7171',
            show: 1,
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<GeneDetailModal {...props} />);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have progress bar while loading', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    it('should get some mock data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/genes/`)
            .reply(200, geneResponse)
            .onGet(new RegExp(`${NCBI_API_BASE}/esummary.fcgi*`))
            .reply(200, ncbiResponse);
        const c = mount(<GeneDetailModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            mockUseEffect()
            c.update()
            //console.log(`GeneDetail: ${c.debug()}`)
            const csvExportM = c.find('ExportCSVMesh')
            expect(csvExportM.length).toEqual(1);
            //csvExportM.simulate('click')
        });
    });

    it('should get NCBI error', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/genes`)
            .reply(200, geneResponse)
            .onGet(new RegExp(`${NCBI_API_BASE}/esummary.fcgi*`))
            .reply(500, {error: "Internal server error"});
        api.parseAPIError = jest.fn().mockReturnValue('NCBI Error');
        const c = mount(<GeneDetailModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
    });

    it('should get fetchGeneDetails error', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/genes`)
            .reply(500, {error: "Internal server error"});
        api.parseAPIError = jest.fn().mockReturnValue('Winnow API Error');
        const c = mount(<GeneDetailModal {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
    });

    /*
    it('should have proper props for progress bar', () => {
        expect(container.find('ProgressBar')).toHaveProp({
            animated: true,
        });
    });
     */
});