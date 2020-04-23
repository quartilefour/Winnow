import React from 'react';
import { shallow } from 'enzyme';
import SearchTermUploader from '../../../components/common/SearchTermUploader';

test('should test SearchTermUploader component', () => {
    const wrapper = shallow(<SearchTermUploader />);
    expect(wrapper).toMatchSnapshot();
});

describe('<SearchTermUploader />', () => {
    const container = shallow(<SearchTermUploader />);
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