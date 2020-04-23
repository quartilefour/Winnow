import React from 'react';
import { shallow } from 'enzyme';
import GeneDetailModal from '../../../components/gene/GeneDetailModal';
import {mountWrap, shallowWrap} from "../../_helpers";

describe('<GeneDetailModal />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;
    const wrappedShallow = () => shallowWrap(<GeneDetailModal {...props} />);
    const wrappedMount = () => mountWrap(<GeneDetailModal {...props} />);

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            geneid: 123456789,
        };
        if (component) component.unmount();

        mockUseEffect();
        wrapper = shallow(<GeneDetailModal {...props} />);
    })

    it('should render with mock data in snapshot', () => {
        //const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
    });

    it('should have progress bar while loading', () => {
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