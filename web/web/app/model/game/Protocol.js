Ext.define('web.model.game.Protocol', {
    extend: 'Ext.data.Model',
    alias: 'protocol',
    fields: [
        { name: 'cmd', type: 'int' },
        { name: 'index', type: 'int' },
        { name: 'params', type: 'auto' }

    ]
});
