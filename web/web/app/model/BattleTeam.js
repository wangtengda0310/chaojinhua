Ext.define('web.model.BattleTeam', {
    extend: 'Ext.data.Model',
    alias: 'BattleTeam',

    fields: [
        'name', 'fightPower',
        { name: 'mons.attack', mapping: 'mons.attack'},
        { name: 'mons.pos', mapping: 'mons.pos'}
    ]
});
