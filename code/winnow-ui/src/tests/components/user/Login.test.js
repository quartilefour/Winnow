import React from 'react';
import { mount, shallow } from 'enzyme';
import Login from '../../../components/user/Login';

const initialProps = {
    location: {state: undefined},
    userEmail: 'jonny@harvard.edu',
    userPassword: 'password1234'
}

describe('<Login />', () => {
    const container = shallow(<Login {...initialProps}/>);
    it('should match the snapshot', () => {
        expect(container.html()).toMatchSnapshot();
    });

    it('should have an email field', () => {
        expect(container.find('FormControl[type="email"]').length).toEqual(1);
    });

    it('should have proper props for email field', () => {
        expect(container.find('FormControl[type="email"]').props()).toEqual({
            autoComplete: "username",
            name: "userEmail",
            onChange: expect.any(Function),
            onKeyUp: expect.any(Function),
            placeholder: 'E-mail Address',
            type: 'email',
            value: ''
        });
    });

    it('should have a password field', () => { /* Similar to above */
        expect(container.find('FormControl[type="password"]').length).toEqual(1);
    });
    it('should have proper props for password field', () => { /* Trimmed for less lines to read */
        expect(container.find('FormControl[type="password"]').props()).toEqual({
            autoComplete: "current-password",
            name: "userPassword",
            onChange: expect.any(Function),
            onKeyUp: expect.any(Function),
            placeholder: 'Password',
            type: 'password',
            value: ''
        });
    });
    it('should have a submit button', () => { /* */
        expect(container.find('Button').length).toEqual(1);
    });
    it('should have proper props for submit button', () => { /* */ });
    it('button click changes color of box', async () => {
        // cache button element
        const button = container.find('Button').last();
        const eventMock = {
            target: {
                getAttribute: function() {
                    return button.props()['data-color']
                }
            }
        };
        // pass mocked event object
        await button.props().onClick(eventMock);
        expect(container.find('.box.red').length).toEqual(0);
    });
});

//describe('<Login /> test effect hooks', () => {
//    const container = mount(<Login/>);
//});
