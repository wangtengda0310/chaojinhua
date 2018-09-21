/**
 * Created by iclockname on 2018/9/25.
 */
Ext.define('web.store.Protocol', {
    extend: 'Ext.data.Store',

    alias: 'store.protocol',

    /*
    Uncomment to use a specific model class
    model: 'web.model.game.Protocol',
    */

    /*
    Fields can also be declared without a model class:
    fields: [
        {name: 'firstName', type: 'string'},
        {name: 'lastName',  type: 'string'},
        {name: 'age',       type: 'int'},
        {name: 'eyeColor',  type: 'string'}
    ]
    */
    fields: [
        { name: 'cmd', type: 'int' },
        { name: 'index', type: 'int' },
        { name: 'params', type: 'auto' }

    ],

    /*
    Uncomment to specify data inline
    */
    data : [
        {cmd: '1002',   index: 1, params: 'Spencer'},
        {cmd: '1003',   index: 2, params: 'Maintz'},
        {cmd: '500',  index: 3, params: '{"gm":"1,1,1"}'}
    ]
});