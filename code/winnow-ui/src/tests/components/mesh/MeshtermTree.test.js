import React from 'react';
import {mount, shallow} from 'enzyme';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {mountWrap, shallowWrap} from "../../_helpers";
import {MeshtermTree} from '../../../components/mesh/MeshtermTree';
import GeneDetailModal from "../../../components/gene/GeneDetailModal";

describe('<MeshtermTree {props}/>', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const responseCat = [
        {
            "categoryId": "A", "name": "Anatomy"
        },
        {
            "categoryId": "B", "name": "Organisms"
        },
        {
            "categoryId": "C", "name": "Diseases"
        }
    ];

    const responseParent = [
        {
            "meshId":"D000785","treeParentId":"C01","treeNodeId":"069","meshName":"Aneurysm, Infected","hasChild":false
        },
        {
            "meshId":"D001170","treeParentId":"C01","treeNodeId":"100","meshName":"Arthritis, Infectious","hasChild":true
        }
    ]

    const responseNode = [
        {
            meshId: "D007239", treeParentId: "", treeNodeId: "C01", meshName: "Infections", hasChild: true
        },
        {
            meshId: "D009369", treeParentId: "", treeNodeId: "C04", meshName: "Neoplasms", hasChild: true
        }
    ]


    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            callback: jest.fn().mockResolvedValue([]),
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<MeshtermTree {...props} />);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should show PageLoader', () => {
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });

    it('should get empty tree', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/category`)
            .reply(500, "Internal server error");
        const c = mount(<MeshtermTree {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
        //console.log(c.debug())
        const emptyDiv = c.find('div[className="super-treeview-no-children-content"]');
        expect(emptyDiv.length).toEqual(1);
        expect(emptyDiv.text()).toEqual("No data found");
    });

    it('should get some mock data', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/category`)
            .reply(200, responseCat);
        const c = mount(<MeshtermTree {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });

        expect(c.find('SuperTreeview').length).toEqual(1);

        const cNode = c.find('input[id="C"]');
        expect(cNode.length).toEqual(1);
    });

    it('should check checkbox', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/category`)
            .reply(200, responseCat);
        const c = mount(<MeshtermTree {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });

        const cNode = c.find('input[id="C"]');
        expect(cNode.length).toEqual(1);

        cNode.simulate('click', {target: {checked: true, isChecked: true}})
        //console.log(c.debug())

    });

    it('should expand node', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/category`)
            .reply(200, responseCat)
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/tree/nodeid/C`)
            .reply(200, responseNode)
            .onGet(`${WINNOW_API_BASE_URL}/meshterms/tree/parentid/C01`)
            .reply(200, responseParent);

        const c = mount(<MeshtermTree {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
            //console.log(`I want to see: ${c.debug()}`)

            const t = c.find('div[className="super-treeview-triangle-btn super-treeview-triangle-btn-right"]')
            console.log(`cat nodes: ${t.length}`)
            t.last().simulate('click')
            c.update()
            //console.log(`I want to see: ${c.debug()}`)
            const cNode = c.find('input[id="C"]').parent();
            expect(cNode.is('div')).toEqual(true)
            //console.log(`parent node contents: ${JSON.stringify(c.find('input[id="C"]'))}`)
            expect(cNode.find('div').length).toEqual(1);
        });


        //cNode.simulate('click')
        //console.log(c.debug())

    });
});
