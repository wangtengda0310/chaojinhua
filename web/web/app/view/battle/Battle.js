/**
 * Created by iclockname on 2018/9/26.
 */
Ext.define('web.view.battle.Battle', {
    xtype:'web.view.battle.Battle',
    extend: 'Ext.Container',

    requires: [
        'web.view.battle.BattleModel',
		'web.view.battle.BattleController'
    ],

    /*
    Uncomment to give this component an xtype
    xtype: 'battle',
    */

    viewModel: {
        type: 'battle'
    },

    controller: 'battle',

    items:[{
        reference: 'view',
        html: '<canvas id="demoCanvas" width="800" height="300">'
            + 'alternate content'
            + '</canvas>'}],
});