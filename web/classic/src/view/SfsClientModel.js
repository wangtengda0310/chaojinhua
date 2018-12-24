Ext.define('web.view.SfsClientModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.sfsclient',
    data : {
		connected:false,
		uid:"10001466",
		serverId:102,
		pwd:"111111",
		cmd:"1003",
		content: '',
		request:{},
		config:{
			host:"192.168.2.189",
			port:9999,debug:false
		}
	}
});
