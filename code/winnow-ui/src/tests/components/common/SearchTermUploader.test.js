import React from 'react';
import { shallow } from 'enzyme';
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import {act} from "react-dom/test-utils";
import SearchTermUploader from '../../../components/common/SearchTermUploader';

describe('<SearchTermUploader />', () => {
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
            active: true,
            searchable: jest.fn(),
            update: jest.fn().mockReturnValue({})
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<SearchTermUploader {...props} />);
    })

    it('should match the snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have queryFormat radio buttons', () => {
        expect(wrapper.find('FormCheck').length).toEqual(6);
    });

    it('should have Mesh name radio button', () => {
        const meshtermButton = wrapper.find('FormCheck[value="name"]')
        expect(meshtermButton.length).toEqual(1);
        meshtermButton.simulate('change', {currentTarget: {name: "queryFormat", value:"name", checked: true}})
    });

    it('should have textarea', () => {
        const textArea = wrapper.find('FormControl[name="textArea"]')
        expect(textArea.length).toEqual(1);
        textArea.simulate('change', {currentTarget: {name: "textArea", value:"Cystic Fibrosis"}})
    });

    it('should have file upload', () => {
        const fileUpload = wrapper.find('FormControl[type="file"]')
        expect(fileUpload.length).toEqual(1);
        fileUpload.simulate('change', {currentTarget: {name: "fileUpload", files: [{name: "terms.csv"}]}})
    });

    it('should have progress bar', () => {
        props.active = false;
        mockUseEffect();
        wrapper = shallow(<SearchTermUploader {...props} />);
        expect(wrapper.find('PageLoader').length).toEqual(1);
    });
    /*

    it('should have proper props for progress bar', () => {
        expect(container.find('ProgressBar')).toHaveProp({
            animated: true,
        });
        expect(container.find('ProgressBar')).toHaveProp({
            now: 100,
        });
        expect(container.find('ProgressBar')).toHaveProp({
            variant: "info",
        });
        expect(container.find('ProgressBar')).toHaveProp({
            label: 'Loading...',
        });
    });
     */
});