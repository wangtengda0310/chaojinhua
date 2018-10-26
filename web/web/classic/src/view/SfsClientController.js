Ext.define('web.view.SfsClientController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.sfsclient',

	index:0,
	history: function(msg) {
		this.lookupReference('history').getStore().insert(0, {type:'log',cmdId: "", content:msg});
	},
	jsonParse: function(a,b,c,d,e,f) {
		console.log(a,b,c,d,e,f);
	},
	onConnection: function(event)
	{
		if (event.success)
		{
			this.history("Connected to SmartFoxServer 2X!<br>SFS2X API version: " + this.sfs.version);

			// Show disconnect button
			this.switchButtons();
			try
			{
				var req = new SFS2X.SFSObject();
				req.putBool("isRepeat",false);
				this.sfs.send(new SFS2X.LoginRequest(this.getViewModel().get("uid"), this.getViewModel().get("pwd"), req, "zoom01"));
			}
			catch(err)
			  {
				this.history(err);
			}
		} else
		{
			this.history("Connection failed: " + (event.errorMessage ? event.errorMessage + " (" + event.errorCode + ")" : "Is the server running at all?"));

			// Reset
			this.reset();
		}
	},
	reset: function()
	{
		// Remove SFS2X listeners
		this.sfs.removeEventListener(SFS2X.SFSEvent.CONNECTION, this.onConnection);
		this.sfs.removeEventListener(SFS2X.SFSEvent.CONNECTION_LOST, this.onConnectionLost);
		
		this.sfs.logger.removeEventListener(SFS2X.LoggerEvent.DEBUG, this.onDebugLogged);
		this.sfs.logger.removeEventListener(SFS2X.LoggerEvent.INFO, this.onInfoLogged);
		this.sfs.logger.removeEventListener(SFS2X.LoggerEvent.WARNING, this.onWarningLogged);
		this.sfs.logger.removeEventListener(SFS2X.LoggerEvent.ERROR, this.onErrorLogged);
		
		sfs = null;
	},
	switchButtons: function()
	{
		console.error("not implements switchButtons function");
	},
	onConnectionLost: function(event)
	{
		console.log(event);
		this.history(event);
        this.lookupReference('history').getStore().insert(0, {type:'error',cmdId: "connection lost", content:event.params.getUtfString("infor")});

		// Hide disconnect button
		this.switchButtons();

		// Reset
		this.reset();
	},
	onExtensionResponse: function(event)
	{
		var message = event.params.getUtfString("infor");
        this.lookupReference('history').getStore().insert(0, {type:'response',cmdId: event.cmd, content:message});
		this.getViewModel().set("content", new JSONFormat(message,4).toString());
	},
	onLoginError: function(evtParams)
	{
        this.lookupReference('history').getStore().insert(0, {type:'error',cmdId: event.cmd, content:event.params.getUtfString("infor")});
	},
	onDebugLogged: function(event)
	{
		this.history(event.message, "DEBUG", true);
	},
	onInfoLogged: function(event)
	{
		this.history(event.message, "INFO", true);
	},
	onWarningLogged: function(event)
	{
		this.history(event.message, "WARN", true);
	},
	onErrorLogged: function(event)
	{
		this.history(event.message, "ERROR", true);
	},	
	disconnect: function()
	{
		// Log message
		this.history("Disconnecting...");

		// Disconnect
		this.sfs.disconnect();
	},
	onLogin: function(event)
	{
	//		var send = new SFS2X.SFSObject();
	//		send.putUtfString("userId",uid);
	//		send.putInt("serverId", serverId);
	//		
	//		var req = new SFS2X.SFSObject();
	//		req.putSFSObject("infor", send);
	//		req.putInt("index", this.index++);
	//		
	//		this.sfs.send(new SFS2X.ExtensionRequest("1002",req));
	},
	toggleDisabled: function (checkbox, checked) {
		var config = this.getViewModel().get('config').debug = checked;
    },
	connect: function() {
		// Log message
		this.history("Connecting...");

		// Create configuration object
		var config = this.getViewModel().get('config');
		config.useSSL = false;

		// Create SmartFox client instance
		this.sfs = new SFS2X.SmartFox(config);

		// Set logging
		this.sfs.logger.level = SFS2X.LogLevel.INFO;
		this.sfs.logger.enableConsoleOutput = true;
		this.sfs.logger.enableEventDispatching = true;

		this.sfs.logger.addEventListener(SFS2X.LoggerEvent.DEBUG, this.onDebugLogged, this);
		this.sfs.logger.addEventListener(SFS2X.LoggerEvent.INFO, this.onInfoLogged, this);
		this.sfs.logger.addEventListener(SFS2X.LoggerEvent.WARNING, this.onWarningLogged, this);
		this.sfs.logger.addEventListener(SFS2X.LoggerEvent.ERROR, this.onErrorLogged, this);

		// Add event listeners
		this.sfs.addEventListener(SFS2X.SFSEvent.CONNECTION, this.onConnection,this);
		this.sfs.addEventListener(SFS2X.SFSEvent.CONNECTION_LOST, this.onConnectionLost, this);
		this.sfs.addEventListener(SFS2X.SFSEvent.LOGIN, this.onLogin, this);
		this.sfs.addEventListener(SFS2X.SFSEvent.EXTENSION_RESPONSE, this.onExtensionResponse,this);
		this.sfs.addEventListener(SFS2X.SFSEvent.LOGIN_ERROR, this.onLoginError, this);

		// Attempt connection
		this.sfs.connect();
	},
	send: function(){
		var cmd = this.getViewModel().get("cmd");
		var txt = this.getViewModel().get("requestContent");
		
		var param = new SFS2X.SFSObject();
		param.putInt("index", this.index++);
		param.putUtfString("infor",txt);
		
		var req = new SFS2X.ExtensionRequest(cmd,param);
		this.sfs.send(req);
	},
	afterRender: function() {
			$('textarea').numberedtextarea();
			var current_json = '';
			var current_json_str = '';
			var xml_flag = false;
			var zip_flag = false;
			var shown_flag = false;
			var compress_flag = false;
			$('.tip').tooltip();
			function init(){
				xml_flag = false;
				zip_flag = false;
				shown_flag = false;
				compress_flag = false;
				renderLine();
				$('.xml').attr('style','color:#999;');
				$('.zip').attr('style','color:#999;');

			}
			$('#json-src').keyup(function(){
				init();
				var content = $.trim($(this).val());
				var result = '';
				if (content!='') {
					//如果是xml,那么转换为json
					if (content.substr(0,1) === '<' && content.substr(-1,1) === '>') {
						try{
							var json_obj = $.xml2json(content);
							content = JSON.stringify(json_obj);
						}catch(e){
							result = '解析错误：<span style="color: #f1592a;font-weight:bold;">' + e.message + '</span>';
							current_json_str = result;
							$('#json-target').html(result);
							return false;
						}

					}
					try{
						current_json = jsonlint.parse(content);
						current_json_str = JSON.stringify(current_json);
						//current_json = JSON.parse(content);
						result = new JSONFormat(content,4).toString();
					}catch(e){
						result = '<span style="color: #f1592a;font-weight:bold;">' + e + '</span>';
						current_json_str = result;
					}

					$('#json-target').html(result);
				}else{
					$('#json-target').html('');
				}

			});
			$('.xml').click(function(){
				if (xml_flag) {
					$('#json-src').keyup();
				}else{
					var result = $.json2xml(current_json);
					$('#json-target').html('<textarea style="width:100%;position:absolute;height: 80vh;min-height:480px;border:0;resize:none;">'+result+'</textarea>');
					xml_flag = true;
					$(this).attr('style','color:#15b374;');
				}

			});
			$('.shown').click(function(){
				if (!shown_flag) {
					renderLine();
					$('#line-num').show();
					$('.numberedtextarea-line-numbers').show();
					shown_flag = true;
					$(this).attr('style','color:#15b374;');
				}else{
					$('#line-num').hide();
					$('.numberedtextarea-line-numbers').hide();
					shown_flag = false;
					$(this).attr('style','color:#999;');
				}
			});
			function renderLine(){
				var line_num = $('#json-target').height()/20;
				$('#line-num').html("");
				var line_num_html = "";
				for (var i = 1; i < line_num+1; i++) {
					line_num_html += "<div>"+i+"<div>";
				}
				$('#line-num').html(line_num_html);
			}
			$('.zip').click(function(){
				if (zip_flag) {
					$('#json-src').keyup();
				}else{
					$('#json-target').html(current_json_str);
					zip_flag = true;
					$(this).attr('style','color:#15b374;');
				}

			});
			$('.compress').click(function(){
				if(!compress_flag){
					$(this).attr('style','color:#15b374;');
					//$(this).attr('title','取消折叠').tooltip('fixTitle').tooltip('show');
					$($(".fa-minus-square-o").toArray().reverse()).click();
					compress_flag = true;
				}else{
					while($(".fa-plus-square-o").length>0){
						$(".fa-plus-square-o").click();
					}
					compress_flag = false;
					$(this).attr('style','color:#555;');
					$(this).attr('title','折叠').tooltip('fixTitle').tooltip('show');
				}
			});
			$('.clear').click(function(){
				 $('#json-src').val('');
				 $('#json-target').html('');
			});
			(function($){
			   $.fn.innerText = function(msg) {
					 if (msg) {
						if (document.body.innerText) {
						   for (var i in this) {
							  this[i].innerText = msg;
						   }
						} else {
						   for (var i in this) {
							  this[i].innerHTML.replace(/&amp;lt;br&amp;gt;/gi,"n").replace(/(&amp;lt;([^&amp;gt;]+)&amp;gt;)/gi, "");
						   }
						}
						return this;
					 } else {
						if (document.body.innerText) {
						   return this[0].innerText;
						} else {
						   return this[0].innerHTML.replace(/&amp;lt;br&amp;gt;/gi,"n").replace(/(&amp;lt;([^&amp;gt;]+)&amp;gt;)/gi, "");
						}
					 }
			   };
			})(jQuery);
			$('.save').click();
			var clipboard = new Clipboard('.copy');
			$('#json-src').keyup();
	},
	copy: function(){
		//$.msg("成功复制到粘贴板","color:#00D69C;");
		// $(this).tooltip('toggle')
		//       .attr('data-original-title', "复制成功！")
		//       .tooltip('fixTitle')
		//       .tooltip('toggle');
	},
	save: function(){
		var text = this.getViewModel().get('content').replace('　　', '    ');
		var blob = new Blob([text], {type: "application/json;charset=utf-8"});
		var timestamp=new Date().getTime();
		saveAs(blob, "format."+timestamp+".json");
	}
});
