/**
 * This view is an example list of people.
 */
Ext.define('web.view.game.GameProtocolList', {
    extend: 'Ext.grid.Panel',
    xtype: 'protocollist',

    requires: [
        'web.store.Protocol'
    ],

    title: 'protocol',

    store: {
        type: 'protocol'
    },

    columns: [
        { text: 'cmd',  dataIndex: 'cmd' },
        { text: 'index', dataIndex: 'index', flex: 1 },
        { text: 'params', dataIndex: 'params', flex: 1 }
    ],

    listeners: {
        select: function(x){alert(x);}
    }
});
