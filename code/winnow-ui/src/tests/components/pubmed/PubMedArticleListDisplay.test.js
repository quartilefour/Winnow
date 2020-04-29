import React from 'react';
import {mount, shallow} from 'enzyme';
import PubMedArticleListDisplay from '../../../components/pubmed/PubMedArticleListDisplay';
import {mountWrap, shallowWrap} from "../../_helpers";
import MockAdapter from "axios-mock-adapter";
import axios from "axios";
import {WINNOW_API_BASE_URL} from "../../../constants";
import BookmarkTab from "../../../components/user/BookmarkTab";
import {act} from "react-dom/test-utils";

describe('<PubMedArticleListDisplay />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const response = {
        "geneId": "7171",
        "meshId": "D003550",
        "results": [{
            "publicationId": "26618866",
            "completedDate": "2016-02-01",
            "dateRevised": "2018-11-13",
            "title": "∆F508 CFTR interactome remodelling promotes rescue of cystic fibrosis.",
            "authors": [{"lastName": "Balch", "foreName": "William E"}, {
                "lastName": "Bamberger",
                "foreName": "Casimir"
            }, {"lastName": "Calzolari", "foreName": "Diego"}, {
                "lastName": "Lavallée-Adam",
                "foreName": "Mathieu"
            }, {"lastName": "Martínez-Bartolomé", "foreName": "Salvador"}, {
                "lastName": "Pankow",
                "foreName": "Sandra"
            }, {"lastName": "Yates", "foreName": "John R"}]
        }]
    };

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    const wrappedShallow = () => shallowWrap(<PubMedArticleListDisplay {...props} />);
    const wrappedMount = () => mountWrap(<PubMedArticleListDisplay {...props} />);
    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {
            history: () => {
            },
            listData: {description: "tropomyosin 3",
                geneId: "7170",
                index: 2,
                meshId: "D003550",
                name: "Cystic Fibrosis",
                publicationCount: 1,
                pvalue: 0.343838274335,
                symbol: "TPM3",
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

    it('should get some mock data', async () => {
        const mockfBM = new MockAdapter(axios);
        mockfBM
            .onPost(`${WINNOW_API_BASE_URL}/publications`)
            .reply(200, response);
        const c = mount(<PubMedArticleListDisplay {...props}/>);
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.update()
        });
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