import React from 'react';
import { mount,shallow } from 'enzyme';
import {mountWrap, shallowWrap} from "../../_helpers";
import {MeshtermTree} from '../../../components/mesh/MeshtermTree';
import Login from "../../../components/user/Login";

test('should test MeshtermTree component', () => {
    const wrappedShallow = () => shallowWrap(<MeshtermTree />)
    const wrapper = wrappedShallow();
    expect(wrapper).toMatchSnapshot();
});