Ext.define('web.store.BattleTeams', {
    extend: 'Ext.data.Store',

    alias: 'store.BattleTeams',

    model: 'web.model.BattleTeam',

    proxy : {
        type: 'ajax',
        url: 'http://localhost:1841/web/resources/testBattleTeamsData.json',
        reader: {
            type: 'json',
            rootProperty: 'teams'
        },
        autoLoad: true
    }
});
