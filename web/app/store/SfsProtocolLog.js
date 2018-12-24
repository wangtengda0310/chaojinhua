Ext.define('web.store.SfsProtocolLog', {
    extend: 'Ext.data.Store',

    alias: 'store.SfsProtocolLog',

    model: 'web.model.SfsProtocol',

    proxy: {
        type: 'memory',
        reader: {
            type: 'json',
            rootProperty: 'items'
        }
    }
});
