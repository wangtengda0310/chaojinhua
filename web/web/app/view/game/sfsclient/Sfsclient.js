/**
 * Created by iclockname on 2018/9/27.
 */
Ext.define('web.view.game.sfsclient.Sfsclient', {
    extend: 'Ext.Container',

    requires: [
        'web.view.game.sfsclient.SfsclientModel',
		'web.view.game.sfsclient.SfsclientController'
    ],

    /*
    Uncomment to give this component an xtype
    */
    xtype: 'sfsclient',

    viewModel: {
        type: 'sfsclient'
    },

    controller: 'sfsclient',

    items: [
        /* include child components here */
        {
            xtype: 'panel',
            items:[
                {id:"params",label:"params",xtype:'textfield'},
                {id:"cmd",label:"cmd",xtype:'textfield'},
                {text:"cmd",xtype: 'button'}
                ]
        }, {
            xtype: 'protocollist'
        }
    ]
});