import React from 'react';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {mount, shallow} from 'enzyme';
import {act} from "react-dom/test-utils";
import BookmarkTab from '../../../components/user/BookmarkTab';
import {WINNOW_API_BASE_URL} from "../../../constants";

describe('<BookmarkTab {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const response = [{
        "searchId": 9,
        "searchName": "RPL35AP4",
        "searchQuery": {
            "symbol": [],
            "geneId": ["100048922"],
            "meshTreeId": [],
            "name": [],
            "description": [],
            "meshId": []
        },
        "createdDate": "2020-04-27T17:06:49.026+0000",
        "updatedAt": "2020-04-27T17:06:49.026+0000"
    }, {
        "searchId": 6, "searchName": "MeSH Neoplasms G1", "searchQuery": {
            "symbol": [],
            "geneId": [],
            "meshTreeId": [],
            "name": ["\"Antibodies, Neoplasm\"", "Bone Marrow Neoplasms", "\"Neoplasms, Bone Tissue\"", "Head and Neck Neoplasms", "Urinary Bladder Neoplasms", "\"Mammary Neoplasms, Experimental\"", "\"Liver Neoplasms, Experimental\"", "\"Antigens, Neoplasm\"", "\"Mammary Neoplasms, Experimental\"", "Colorectal Neoplasms", "\"RNA, Neoplasm\"", "Esophageal Neoplasms", "Triple Negative Breast Neoplasms", "Neoplasms", "Neoplasms", "\"RNA, Neoplasm\"", "Stomach Neoplasms", "Colonic Neoplasms", "Bone Neoplasms", "\"Antigens, Neoplasm\"", "\"RNA, Neoplasm\"", "Neoplasm Proteins", "Breast Neoplasms", "Brain Neoplasms", "Breast Neoplasms", "Lung Neoplasms", "Colonic Neoplasms", "Prostatic Neoplasms", "Neoplasm Proteins", "Brain Neoplasms", "Neoplasms", "\"Antigens, Neoplasm\"", "Neoplasm Invasiveness", "Prostatic Neoplasms", "Lung Neoplasms", "\"DNA, Neoplasm\"", "Esophageal Neoplasms", "\"RNA, Neoplasm\"", "Neoplasm Proteins", "\"DNA, Neoplasm\"", "Prostatic Neoplasms", "Breast Neoplasms", "Neoplasm Metastasis", "Neoplasms", "\"DNA, Neoplasm\"", "Gastrointestinal Neoplasms", "Colonic Neoplasms", "Bile Duct Neoplasms", "Prostatic Neoplasms", "Neoplasm Transplantation", "Colorectal Neoplasms", "Liver Neoplasms", "Neoplasms", "Liver Neoplasms", "Neoplasm Proteins", "Breast Neoplasms", "Prostatic Neoplasms", "Colorectal Neoplasms", "Neoplasm Proteins", "Liver Neoplasms", "Lung Neoplasms"],
            "description": [],
            "meshId": []
        }, "createdDate": "2020-04-26T22:58:50.196+0000", "updatedAt": "2020-04-26T22:58:50.196+0000"
    }, {
        "searchId": 5,
        "searchName": "gene group1",
        "searchQuery": {
            "symbol": [],
            "geneId": ["23598", "340618", "136227", "23554", "285527", "339988", "101929114", "118429", "222166", "23553", "27124", "285525", "28996", "338917", "100287704", "101928283", "107986827", "1174", "168391", "221946", "23177", "23541", "26100", "27072", "27445", "285501", "286554", "28983", "3201", "3361", "100129603", "100286906", "10135", "101928211", "10344", "10793", "11333", "116984", "152926", "155370", "201965", "219578", "22798", "23001", "23284", "23481", "25798", "26031", "26872", "27010", "2713", "2737", "2791", "285175", "285878", "286184", "2887", "2895", "29896", "3200", "3204", "3209", "100101267", "100128553", "100134869", "100144602", "100289678", "100506527", "10157", "101927547", "10282", "10291", "10447", "10457", "10842", "11112", "113878", "114805", "136895", "140545", "155060", "155066", "168433", "1804", "2020", "2056", "222235", "222256", "22976", "22998", "23231", "23233", "23386", "23409", "246126", "246721", "2589", "26024", "26261", "346606"],
            "meshTreeId": [],
            "name": [],
            "description": [],
            "meshId": []
        },
        "createdDate": "2020-04-26T22:50:56.310+0000",
        "updatedAt": "2020-04-26T22:50:56.310+0000"
    }, {
        "searchId": 3,
        "searchName": "Cystic Fibrosis",
        "searchQuery": {
            "symbol": [],
            "geneId": [],
            "meshTreeId": ["C06.689.202"],
            "name": [],
            "description": [],
            "meshId": []
        },
        "createdDate": "2020-04-26T00:22:35.775+0000",
        "updatedAt": "2020-04-26T00:22:35.775+0000"
    }, {
        "searchId": 2,
        "searchName": "Spondylitis",
        "searchQuery": {
            "symbol": [],
            "geneId": [],
            "meshTreeId": ["C01.160.762", "C01.160.762.301"],
            "name": [],
            "description": [],
            "meshId": []
        },
        "createdDate": "2020-04-25T12:42:55.484+0000",
        "updatedAt": "2020-04-25T12:42:55.484+0000"
    }]

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
        const mockfBM = new MockAdapter(axios);
        mockfBM
            .onGet(`${WINNOW_API_BASE_URL}/bookmarks`)
            .reply(200, response);
        const c = mount(<BookmarkTab/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug());
    })
});
