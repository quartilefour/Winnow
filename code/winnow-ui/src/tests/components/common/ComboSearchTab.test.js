import React from 'react';
import {shallow} from 'enzyme';
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

    /*
    it('should have progress bar', () => {
        expect(container.find('ProgressBar').length).toEqual(1);
    });

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