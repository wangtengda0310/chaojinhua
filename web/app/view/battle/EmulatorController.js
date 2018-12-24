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
        var updatePos = function(mon) {
            mon.x += mon.v;
            if(mon.x<=0 || mon.x >= 800) {
                mon.v *= -1;
            }
        };
        var selfMs = viewModel.selfMs;
        for(var i in selfMs) {
            updatePos(selfMs[i]);
        }

        var opponentMs = viewModel.opponentMs;
        for(var i in opponentMs) {
            updatePos(opponentMs[i]);
        }

        var selfFront,opponentFront;
        for(i in viewModel.selfMs) {
            if(!selfFront || viewModel.selfMs[i].x>selfFront.x) {
                selfFront = viewModel.selfMs[i];
            }
        }
        for(i in viewModel.opponentMs) {
            if(!opponentFront || viewModel.opponentMs[i].x<opponentFront.x) {
                opponentFront = viewModel.opponentMs[i];
            }
        }
        if(selfFront && opponentFront && selfFront.x>=opponentFront.x){
            selfFront.v*=-1;
            opponentFront.v*=-1;
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

        mon.id = viewModel.selfMs.length + viewModel.opponentMs.length + 1;
    }
    , addRightM: function() {
        var mon = new createjs.Shape();
        mon.v = 7;
        mon.graphics.beginFill("red").drawCircle(0, 0, 50);
        mon.x = 500;
        mon.y = 100;
        viewModel.opponentMs.push(mon);
        viewModel.stage.addChild(mon);

    }
});
