import React from 'react';
import { shallow } from 'enzyme';
import PageLoader from '../../../components/common/PageLoader';


describe('<PageLoader />', () => {
    let props;
    let wrapper;
    let component;

    beforeEach(() => {
        props = {
            message: 'Loading...',
            now: 100,
            varient: 'info'
        };
        if (component) component.unmount();

        wrapper = shallow(<PageLoader {...props}/>);
    });

    it('should match the snapshot', () => {
        expect(wrapper.html()).toMatchSnapshot();
    });

    it('should have progress bar', () => {
        expect(wrapper.find('ProgressBar').length).toEqual(1);
    });

    it('should have proper props for progress bar', () => {
        expect(wrapper.find('ProgressBar')).toHaveProp({
            animated: true,
        });
        expect(wrapper.find('ProgressBar')).toHaveProp({
            now: 100,
        });
        expect(wrapper.find('ProgressBar')).toHaveProp({
            variant: "info",
        });
        expect(wrapper.find('ProgressBar')).toHaveProp({
            label: 'Loading...',
        });
    });
});