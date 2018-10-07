Ext.define('web.view.battle.EmulatorController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.battle-emulator',

    afterRender: function(view) {
        var model = view.viewModel.data;
        viewModel = model;
        self = this;
        model.v = 5;
        self.v2 = -5;
        createjs.Ticker.timingMode = createjs.Ticker.RAF;
        createjs.Ticker.addEventListener("tick", this.tick);

        viewModel.stage = new createjs.Stage("demoCanvas");

    }
    , tick: function(event) {
        var hitSomthing = function(mon,opponentMs) {

        };
        var updatePos = function(mon) {
            mon.x += mon.v;
            if(mon.x<=0 || mon.x >= 800 || hitSomthing(mon)) {
                mon.v *= -1;
            }
        };
        var selfMs = viewModel.selfMs;
        for(i in selfMs) {
            updatePos(selfMs[i]);
        }

        var opponentMs = viewModel.opponentMs;
        for(i in opponentMs) {
            updatePos(opponentMs[i]);
        }

        viewModel.stage.update();
    }
    , addLeftM: function() {
        var mon = new createjs.Shape();
        mon.v = 5;
        mon.graphics.beginFill("blue").drawCircle(0, 0, 50);
        mon.x = 100;
        mon.y = 100;
        viewModel.selfMs.push(mon);
        viewModel.stage.addChild(mon);

    }
    , addRightM: function() {
        var mon = new createjs.Shape();
        mon.v = 10;
        mon.graphics.beginFill("red").drawCircle(0, 0, 50);
        mon.x = 500;
        mon.y = 100;
        viewModel.opponentMs.push(mon);
        viewModel.stage.addChild(mon);

    }
});
