/**
 * Created by iclockname on 2018/9/26.
 * https://www.sencha.com/forum/showthread.php?268103-how-to-put-an-image-onto-a-canvas-inside-an-extjs-form-or-window
 */
Ext.define('web.view.battle.EaselWindow', {
    xtype:   'battle.EaselWindow',
    // width: 500,
    // height: 500,
    // extend: 'Ext.Window'
    //
    // ,html: '<canvas id="demoCanvas" width="500" height="500">'
    //     + 'alternate content'
    //     + '</canvas>'
    //
    // ,afterRender: function() {
    //     this.callParent(arguments);
    //
    //
    //
    //
    //     var stage = new createjs.Stage("demoCanvas");
    //     var myImage = new createjs.Bitmap("dbz.jpg");
    //     stage.addChild(myImage);
    //     stage.update();
    //
    // } // end after render func

    extend: 'Ext.Container'

    ,html: '<canvas id="demoCanvas" width="500" height="300">'
        + 'alternate content'
        + '</canvas>'

    ,afterRender: function() {
        this.callParent(arguments);

        var stage = new createjs.Stage("demoCanvas");

        var circle = new createjs.Shape();
        circle.graphics.beginFill("red").drawCircle(0, 0, 50);
        circle.x = 100;
        circle.y = 100;
        stage.addChild(circle);

        stage.update();
    }
}); // end define