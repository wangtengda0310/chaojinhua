
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
	defaults: {
		frame: true,
		margin: 0.5
	},
    items: [
	{
		columnWidth:0.3,
	tbar: {
		layout: {
			type: 'table',
			columns: 2,
			tdAttrs: { style: 'padding: 10px; vertical-align: top;' }
		},
		items:[
			{xtype: 'textfield', emptyText:"uid", bind:{value:'{uid}',editable:'{!connected}'}},
			{xtype: 'textfield', emptyText:"host", bind:{value:'{config.host}',editable:'{!connected}'}},
			{xtype: 'textfield', emptyText:"port", bind:'{config.port}'},
			{xtype: 'textfield', emptyText: '暂时没用', listeners:{paste:'jsonParse'}},
		]
	},

		items: [
			{xtype: 'textfield', label:"cmd", bind:'{cmd}', enableKeyEvents: true, listeners:{keyup: 'addParamField'}},
			{
				xtype:'grid',
				reference: 'requestParams',
				plugins: {
					cellediting: {
						clicksToEdit: 1
					}
				},
				store: {
				},
				columns: [
					{text:'参数', dataIndex:'property',editor: {allowBlank: false}}
					,{dataIndex:'value',editor: {allowBlank: true}}
				]
			},
			{
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
			{xtype:'grid'
			, height:500
			, reference: 'history'
			,store:{type:'SfsProtocolLog'}
			,columns:[{text:'type',dataIndex:'type'},{text:'reqcmd',dataIndex:'reqcmdId'},{text:'rescmd',dataIndex:'rescmdId'},{text:'content',dataIndex:'content'}]
			,listeners : {
				itemdblclick: 'historyDoubleClick',
				itemclick: 'historyClick'}
				}
		]
	},
	{
		columnWidth:0.7,
		
		tbar:{
			items:[
				{xtype:'button',text:'压缩', listeners:{click:'zip'}},
				{xtype:'button',text:'转XML', listeners:{click:'xml'}},
				{xtype:'button',text:'显示行号', listeners:{click:'shown'}},
				{xtype:'button',text:'清空', listeners:{click:'clear'}},
				{xtype:'button',text:'保存', listeners:{click:'save'}},
				{xtype:'button',text:'复制', listeners:{click:'copy'}},
				{xtype:'button',text:'折叠', listeners:{click:'compress'}}
			]
		},
		
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
		items:[{height:500,bind:{html:'<div style="max-height: 100%;overflow-y: scroll;">{content}</div>'}}]
	}]
});
