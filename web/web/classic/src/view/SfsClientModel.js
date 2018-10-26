Ext.define('web.view.SfsClientModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.sfsclient',
    data : {
		connected:false,
		uid:"10001466",
		pwd:"111111",
		cmd:"1003",
		content:"model content",
		requestContent:'{"userId":10001466,"serverId":102}',
		config:{
		host:"192.168.2.234",
		port:9999,debug:false}
	}
});
