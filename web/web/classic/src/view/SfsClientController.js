Ext.define('web.view.SfsClientController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.sfsclient',

	connect: function() {
		// Clear log window
		document.getElementById("json-target").innerHTML = "";

		// Disable interface
		enableInterface(false);

		// Log message
		history("Connecting...");

		// Create configuration object
		var config = {};
		config.host = document.getElementById("addressIn").value;
		config.port = Number(document.getElementById("portIn").value);
		config.debug = document.getElementById("debugCb").checked;
		config.useSSL = false;

		// Create SmartFox client instance
		sfs = new SFS2X.SmartFox(config);

		// Set logging
		sfs.logger.level = SFS2X.LogLevel.INFO;
		sfs.logger.enableConsoleOutput = true;
		sfs.logger.enableEventDispatching = true;

		sfs.logger.addEventListener(SFS2X.LoggerEvent.DEBUG, onDebugLogged, this);
		sfs.logger.addEventListener(SFS2X.LoggerEvent.INFO, onInfoLogged, this);
		sfs.logger.addEventListener(SFS2X.LoggerEvent.WARNING, onWarningLogged, this);
		sfs.logger.addEventListener(SFS2X.LoggerEvent.ERROR, onErrorLogged, this);

		// Add event listeners
		sfs.addEventListener(SFS2X.SFSEvent.CONNECTION, onConnection, this);
		sfs.addEventListener(SFS2X.SFSEvent.CONNECTION_LOST, onConnectionLost, this);
		sfs.addEventListener(SFS2X.SFSEvent.LOGIN, onLogin, this);
		sfs.addEventListener(SFS2X.SFSEvent.EXTENSION_RESPONSE, onExtensionResponse, this);
		sfs.addEventListener(SFS2X.SFSEvent.LOGIN_ERROR, onLoginError, this);

		// Attempt connection
		sfs.connect();
	},
	addData: function(){
		console.log(this.getStore());
		console.log(this.lookupReference('history'));
		console.log(this.lookupReference('history').getStore());
		var cmd = document.getElementById("cmd").value;
		var txt = document.getElementById("param").value;
		
		var param = new SFS2X.SFSObject();
		param.putInt("index", index++);
		param.putUtfString("infor",txt);
		
		var req = new SFS2X.ExtensionRequest(cmd,param);
		history(cmd+":  "+txt);
		sfs.send(req);
        this.lookupReference('history').getStore().insert(0, {cmdId:"123",content:'test'});
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
			$('.save').click(function(){
				// var content = JSON.stringify(current_json);
				// $('#txt-content').val(content);
				//var text = "hell world";
				var html = $('#json-target').html().replace(/\n/g,'<br/>').replace(/\n/g,'<br>');
				var text = $('#json-target').innerText().replace('　　', '    ');
				var blob = new Blob([text], {type: "application/json;charset=utf-8"});
				var timestamp=new Date().getTime();
				saveAs(blob, "format."+timestamp+".json");
			});
			$('.copy').click(function(){
				//$.msg("成功复制到粘贴板","color:#00D69C;");
				// $(this).tooltip('toggle')
				//       .attr('data-original-title', "复制成功！")
				//       .tooltip('fixTitle')
				//       .tooltip('toggle');
			});
			var clipboard = new Clipboard('.copy');
			$('#json-src').keyup();
	}
});
