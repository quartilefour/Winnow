import React from 'react';
import { shallow } from 'enzyme';
import ComboSearchTab from '../../../components/common/ComboSearchTab';

test('should test ComboSearchTab component', () => {
    const wrapper = shallow(<ComboSearchTab />);
    expect(wrapper).toMatchSnapshot();
});

describe('<ComboSearchTab />', () => {
    const container = shallow(<ComboSearchTab />);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
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