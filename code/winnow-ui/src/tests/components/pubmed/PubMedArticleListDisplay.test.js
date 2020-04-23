import React from 'react';
import { shallow } from 'enzyme';
import PubMedArticleListDisplay from '../../../components/pubmed/PubMedArticleListDisplay';
import {mountWrap, shallowWrap} from "../../_helpers";

describe('<PubMedArticleListDisplay />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    const wrappedShallow = () => shallowWrap(<PubMedArticleListDisplay {...props} />);
    const wrappedMount = () => mountWrap(<PubMedArticleListDisplay {...props} />);
    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
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

        mockUseEffect();
        wrapper = shallow(<PubMedArticleListDisplay {...props} />);
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });
    it('should have PageLoader', () => {
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