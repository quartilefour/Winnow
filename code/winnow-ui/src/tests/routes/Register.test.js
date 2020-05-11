import React from 'react';
import {BrowserRouter as Router} from "react-router-dom";
import Register from '../../routes/Register';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {act} from "react-dom/test-utils";
import {WINNOW_API_BASE_URL} from "../../constants";
import {mount, shallow} from "enzyme";
import * as api from "../../service/ApiService";


describe('<Register />', () => {
    let props;
    let wrapper;
    let useEffect;
    let component;

    const mockUseEffect = () => {
        useEffect.mockImplementationOnce(f => f());
    }

    beforeEach(() => {
        useEffect = jest.spyOn(React, "useEffect");
        props = {};
        if (component) component.unmount();
        mockUseEffect();
        wrapper = shallow(<Register {...props} />)
    })

    it('should render with mock data in snapshot', () => {
        expect(wrapper).toMatchSnapshot();
    });

    it('should have an email field', () => {
        expect(wrapper.find('FormControl[type="email"]').length).toEqual(1);
    });

    it('should have proper props for email field', () => {
        expect(wrapper.find('FormControl[type="email"]').props()).toEqual({
            autoComplete: "username",
            name: "userEmail",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'E-mail Address',
            type: 'email',
            value: ''
        });
    });

    it('should have a password field', () => {
        expect(wrapper.find('FormControl[type="password"]').length).toEqual(2);
    });

    it('should have proper props for password field', () => {
        expect(wrapper.find('FormControl[type="password"]').first().props()).toEqual({
            autoComplete: "new-password",
            name: "userPassword",
            onChange: expect.any(Function),
            onBlur: expect.any(Function),
            placeholder: 'Password',
            type: 'password',
            value: ''
        });
    });

    it('should have a submit button', () => {
        expect(wrapper.find('Button').length).toEqual(1);
    });

    it('should submit credentials when button is clicked', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/registration`)
            .reply(201)
        //const c = mount(<Router><Register {...props}/></Router>)
        const c = mount(<Router><Register/></Router>)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.find('FormControl[name="firstName"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'firstName',
                    value: 'Jonny'
                }
            })
            c.find('FormControl[name="lastName"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'lastName',
                    value: 'Harvard'
                }
            })
            c.find('FormControl[name="userEmail"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'userEmail',
                    value: 'jonny@harvard.edu'
                }
            })
            c.find('FormControl[name="userPassword"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'userPassword',
                    value: 'Test1234!'
                }
            })
            c.find('FormControl[name="passwordConfirm"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'passwordConfirm',
                    value: 'Test1234!'
                }
            })
            expect(c.find('FormControl[name="firstName"]').props().value).toEqual('Jonny')
            expect(c.find('FormControl[name="lastName"]').props().value).toEqual('Harvard')
            expect(c.find('FormControl[name="userEmail"]').props().value).toEqual('jonny@harvard.edu')
            c.find('Form').simulate('submit')

            c.update()
            //mock.reset()
        });

    });

    it('should submit credentials with existing email when button is clicked', async () => {
        const mock = new MockAdapter(axios);
        mock
            .onPost(`${WINNOW_API_BASE_URL}/registration`)
            .reply(409, {error: 'E-mail address already registered'})
        api.parseAPIError = jest.fn().mockReturnValue('E-mail address already registered');
        const c = mount(<Router><Register {...props}/></Router>)
        await act(async () => {
            await Promise.resolve(c);
            await new Promise(resolve => setImmediate(resolve));
            c.find('FormControl[name="firstName"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'firstName',
                    value: 'Jonny'
                }
            })
            c.find('FormControl[name="lastName"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'lastName',
                    value: 'Harvard'
                }
            })
            c.find('FormControl[name="userEmail"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'userEmail',
                    value: 'jonny@harvard.edu'
                }
            })
            c.find('FormControl[name="userPassword"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'userPassword',
                    value: 'Test1234!'
                }
            })
            c.find('FormControl[name="passwordConfirm"]').simulate('change', {
                persist: () => {
                },
                target: {
                    name: 'passwordConfirm',
                    value: 'Test1234!'
                }
            })
            expect(c.find('FormControl[name="firstName"]').props().value).toEqual('Jonny')
            expect(c.find('FormControl[name="lastName"]').props().value).toEqual('Harvard')
            expect(c.find('FormControl[name="userEmail"]').props().value).toEqual('jonny@harvard.edu')
            c.find('Form').simulate('submit')

            //console.log(c.debug());
            c.update()
            //mock.reset()
        });

    });
});