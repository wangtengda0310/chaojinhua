Ext.define('web.view.SfsClientModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.sfsclient',
    stores: {
        history: {
			model: 'SfsClientProtocol',
			autoLoad: true
		}
    }

});
