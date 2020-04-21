import React from 'react';
import { shallow } from 'enzyme';
import PageLoader from '../../../components/common/PageLoader';

test('should test PageLoader component', () => {
    const wrapper = shallow(<PageLoader />);
    expect(wrapper).toMatchSnapshot();
});

describe('<PageLoader />', () => {
    const container = shallow(<PageLoader />);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
    });

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
});