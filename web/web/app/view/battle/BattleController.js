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
        self = this;
        self.v = 5;
        createjs.Ticker.timingMode = createjs.Ticker.RAF;
        createjs.Ticker.addEventListener("tick", this.tick);
    }
    ,afterRender: function() {
        self.stage = new createjs.Stage("demoCanvas");

        self.circle = new createjs.Shape();
        self.circle.graphics.beginFill("red").drawCircle(0, 0, 50);
        self.circle.x = 100;
        self.circle.y = 100;
        self.stage.addChild(self.circle);
        self.stage.update(event);
    }
    ,tick: function(event) {
        var view = self.lookupReference('view');
        self.circle.x+=self.v;
        if (self.circle.x>=500 || self.circle.x<=0)
            self.v = -self.v;
        self.stage.update(event);
    }
});