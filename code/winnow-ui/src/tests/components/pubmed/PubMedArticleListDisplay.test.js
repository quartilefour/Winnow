import React from 'react';
import { shallow } from 'enzyme';
import PubMedArticleListDisplay from '../../../components/pubmed/PubMedArticleListDisplay';
import {mountWrap, shallowWrap} from "../../_helpers";

describe('<PubMedArticleListDisplay />', () => {
    let props;
    let component;
    const wrappedShallow = () => shallowWrap(<PubMedArticleListDisplay {...props} />);
    const wrappedMount = () => mountWrap(<PubMedArticleListDisplay {...props} />);
    beforeEach(() => {
        props = {
            history: () => {},
            listData: {
                geneId: '',
                meshId: '',
                symbol: '',
                name: ''
            }
        };
        if (component) component.unmount();
    })

    test('should render with mock data in snapshot', () => {
        const wrapper = wrappedShallow();
        expect(wrapper).toMatchSnapshot();
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