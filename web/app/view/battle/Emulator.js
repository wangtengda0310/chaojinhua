Ext.define('web.view.battle.Emulator',{
    extend: 'Ext.panel.Panel',
    xtype: 'battleView',

    requires: [
        'web.view.battle.EmulatorController',
        'web.view.battle.EmulatorModel'
    ],

    // requiredScripts: [
    //     '//code.createjs.com/1.0.0/createjs.min.js'
    // ],

    controller: 'battle-emulator',
    viewModel: {
        type: 'battle-emulator'
    },


    items: [{html: '<canvas id="demoCanvas" width="800" height="300" />'},
            {xtype:'button',
             text:'添加左侧怪物',
             listeners:{click:'addLeftM'}},
            {xtype:'button',
             text:'添加右侧怪物',
             listeners:{click:'addRightM'}},
            {items:[{
                xtype:'grid',
                store:{model: 'web.model.BattleTeam',
                       proxy: {type: 'ajax',
                           url:'http://localhost:1841/web/resources/testBattleTeamsData.json',
                           reader: {type:'json', rootProperty:'teams'},
                           listeners:{beforeLoad:function(){alert(1);}},
                           autoLoad: true}
                },
                columns:[{text: 'team', dataIndex: 'name'},{text: 'fightPower', dataIndex: 'fightPower'}]
            }]},
            {items:[{
                xtype:'grid',
                store:{type: 'BattleTeams'},
                columns:[
                    {text: 'name', dataIndex: 'name'},
                    {text: 'mons', dataIndex: 'mons'},
                    {text: 'mons.attack', dataIndex: 'mons.attack'},
                    {
                        text: 'pos',
                        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                            console.log(value, rowIndex, colIndex, record.data.mons.pos);
                            return record.data.mons.pos;
                        }
                    }
                    ,{text: 'mons', dataIndex: 'mons'}
                    ,{text: 'mons.pos', dataIndex: 'mons.pos'}
                    ,{text: 'attack', dataIndex: '{mons.attack}'}
                    ,{text: 'health', dataIndex: 'mons.health'}]
            }]}]
});
