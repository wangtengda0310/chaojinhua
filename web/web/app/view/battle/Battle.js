/**
 * Created by iclockname on 2018/9/26.
 */
Ext.define('web.view.battle.Battle', {
    xtype:'web.view.battle.Battle',
    extend: 'Ext.Container',

    requires: [
        'web.view.battle.BattleModel',
		'web.view.battle.BattleController',
        'web.view.d3.custom.canvas.Particles'
    ],

    /*
    Uncomment to give this component an xtype
    xtype: 'battle',
    */

    viewModel: {
        type: 'battle'
    },

    controller: 'battle',

    items: [
        /* include child components here */
        {
            xtype:'battle.EaselWindow'
        },
        {
            xtype:'d3-view-particles'
        }
    ]
});