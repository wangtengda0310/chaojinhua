
Ext.define('web.view.SfsClient',{
    extend: 'Ext.panel.Panel',
    xtype: 'SfsClient',

    requires: [
        'web.view.SfsClientController',
        'web.view.SfsClientModel',
		'web.store.SfsProtocolLog'
    ],
	
    controller: 'sfsclient',
    viewModel: 'sfsclient',

    layout: 'column',
    items: [
	{columnWidth:0.3,
		items: [{height:300,html: '<div class="col-md-5" style="padding:0px;height:100%;">'+
			'<div id="controls">'+
				'<input type="text" id="addressIn" placeholder="Enter address" value="localhost" />'+
				'<input type="text" id="portIn" placeholder="Enter port" value="9999" /><br/>'+
				'<input type="checkbox" id="debugCb" /> Show debug<br/>'+
				'<input type="button" id="connectBt" value="Connect" onclick="onConnectBtClick()" /><br/>'+
				'<input type="button" id="disconnectBt" value="Disconnect" onclick="onDisconnectBtClick()" style="display: none;" /><br/>'+
				'<input type="text" id="cmd" placeholder="cmd" value="1003" />'+
				'<input type="button" id="sendBt" value="send" onclick="onSendBtClick()" /><br/>'+
				'<textArea id="param" >{"userId":10001466,"serverId":102}</textArea><br/>'+
			'</div>'},
			{xtype:'button',text:'connect',listeners:{click:'connect'}},
			{xtype:'button',text:'send',listeners:{click:'addData'}},
		{xtype:'grid', reference: 'history',store:{type:'SfsProtocolLog'},columns:[{text:'cmd',dataIndex:'cmdId'}]}]
	},
	{columnWidth:0.7,
        html: '<textarea id="json-src" placeholder="在此输入json字符串或XML字符串..."></textarea>'+
    '</div>'+
    '<div>'+
        '<div  class="tool">'+
            '<a href="#" class="tip zip" title="压缩"  data-placement="bottom"><i class="fa fa-database"></i></a>'+
            '<a href="#" class="tip xml" title="转XML"  data-placement="bottom"><i class="fa fa-file-excel-o"></i></a>'+
            '<a href="#" class="tip shown"  title="显示行号"  data-placement="bottom"><i class="glyphicon glyphicon-sort-by-order"></i></a>'+
            '<a href="#" class="tip clear" title="清空"  data-placement="bottom"><i class="fa fa-trash"></i></a>'+
            '<a href="#" class="tip save" title="保存"  data-placement="bottom"><i class="fa fa-download"></i></a>'+
            '<a href="#" class="tip copy" title="复制" data-clipboard-target="#json-target"  data-placement="bottom"><i class="fa fa-copy"></i></a>'+
            '<a href="#" class="tip compress" title="折叠"  data-placement="bottom"><i class="fa fa-compress"></i></a>'+
        '</div>'+
        '<div id="right-box">'+
            '<div id="line-num">'+
                '<div>0</div>'+
            '</div>'+
            '<div class="ro" id="json-target" />'+
        '</div>'+
    '</div>'}]
});
