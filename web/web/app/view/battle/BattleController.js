/**
 * Created by iclockname on 2018/9/26.
 */
Ext.define('web.view.battle.BattleController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.battle',

    /**
     * Called when the view is created
     */
    init: function() {

    }
    ,afterRender: function() {
        var stage = new createjs.Stage("demoCanvas");

        var circle = new createjs.Shape();
        circle.graphics.beginFill("red").drawCircle(0, 0, 50);
        circle.x = 100;
        circle.y = 100;
        stage.addChild(circle);

        stage.update();
    }
});