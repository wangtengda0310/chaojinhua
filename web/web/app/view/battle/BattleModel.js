/**
 * Created by iclockname on 2018/9/26.
 */
Ext.define('web.view.battle.BattleModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.battle',

    stores: {
        /*
        A declaration of Ext.data.Store configurations that are first processed as binds to produce an effective
        store configuration. For example:

        users: {
            model: 'Battle',
            autoLoad: true
        }
        */
    },

    data: {
        /* This object holds the arbitrary data that populates the ViewModel and is then available for binding. */
        shapes:[]
    }
});