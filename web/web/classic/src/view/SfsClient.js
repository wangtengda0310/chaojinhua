
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
	tbar: {
		items:[
			{xtype: 'textfield', label:"host", bind:{value:'{config.host}',editable:'{!connected}'}},
			{xtype: 'textfield', label:"port", bind:'{config.port}'},
			{xtype: 'textfield', label:"cmd", bind:'{cmd}'},
			{xtype: 'textarea', bind:'{requestContent}'},
			{xtype: 'textfield', listeners:{paste:'jsonParse'}},
		]
	},

    layout: 'column',
    items: [
	{
		columnWidth:0.3,

		items: [{
				xtype: 'checkbox',
				boxLabel: 'Show debug',
				margin: '0 0 0 10',
				listeners: {
					change: 'toggleDisabled'
				}
			},
			{xtype:'button',text:'connect',listeners:{click:'connect'}},
			{xtype:'button',text:'disconnect',listeners:{click:'disconnect'}},
			{xtype:'button',text:'send',listeners:{click:'send'}},
			{xtype:'grid', reference: 'history',store:{type:'SfsProtocolLog'},columns:[{text:'type',dataIndex:'type'},{text:'cmd',dataIndex:'cmdId'},{text:'content',dataIndex:'content'}]}
		]
	},
	{
		columnWidth:0.7,
		
		tbar:{items:[
		{xtype:'button',text:'压缩', listeners:{click:'zip'}},
		{xtype:'button',text:'转XML', listeners:{click:'xml'}},
		{xtype:'button',text:'显示行号', listeners:{click:'shown'}},
		{xtype:'button',text:'清空', listeners:{click:'clear'}},
		{xtype:'button',text:'保存', listeners:{click:'save'}},
		{xtype:'button',text:'复制', listeners:{click:'copy'}},
		{xtype:'button',text:'折叠', listeners:{click:'compress'}}
		]},
		
			html:
		'<div>'+
			'<div id="right-box">'+
				'<div id="line-num">'+
					'<div>0</div>'+
				'</div>'+
				'<div class="ro" id="json-target" />'+
			'</div>'+
		'</div>'
		,
		items:[{xtype: 'textarea', bind:{html:'{content}'}}]
	}]
});
