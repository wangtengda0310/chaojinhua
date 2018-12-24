Ext.define('web.model.Monster', {
    extend: 'Ext.data.Model',

    fields: [
        { name: 'pos', type: 'int' },
        { name: 'attack', type: 'int' },
        { name: 'health', type: 'int' },
        { name: 'speed', type: 'int' },
        { name: 'bodySize', type: 'int' },
        { name: 'pushPower', type: 'int' }

    ]
});
