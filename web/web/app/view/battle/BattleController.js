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
        self = this;
        self.v = 5;
        self.v2 = -5;
        createjs.Ticker.timingMode = createjs.Ticker.RAF;
        createjs.Ticker.addEventListener("tick", this.tick);
        self.stage = new createjs.Stage("demoCanvas");

        self.circle = new createjs.Shape();
        self.circle.graphics.beginFill("blue").drawCircle(0, 0, 50);
        self.circle.x = 100;
        self.circle.y = 100;
        self.stage.addChild(self.circle);

        self.circle2 = new createjs.Shape();
        self.circle2.graphics.beginFill("red").drawCircle(0, 0, 50);
        self.circle2.x = 500;
        self.circle2.y = 100;
        self.stage.addChild(self.circle2);

        self.stage.update(event);
    }
    ,tick: function(event) {
        var view = self.lookupReference('view');
        self.circle.x+=self.v;
        if (self.circle.x>=500 || self.circle.x<=0)
            self.v = -self.v;

        self.circle2.x+=self.v2;
        if (self.circle2.x>=500 || self.circle2.x<=0)
            self.v2 = -self.v2;

        self.stage.update(event);
    }
});