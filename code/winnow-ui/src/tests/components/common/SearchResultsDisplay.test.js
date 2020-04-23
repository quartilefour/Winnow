import React from 'react';
import { shallow } from 'enzyme';
import SearchResultsDisplay from '../../../components/common/SearchResultsDisplay';

test('should test SearchResultsDisplay component', () => {
    const wrapper = shallow(<SearchResultsDisplay />);
    expect(wrapper).toMatchSnapshot();
});

describe('<SearchResultsDisplay />', () => {
    const container = shallow(<SearchResultsDisplay />);
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