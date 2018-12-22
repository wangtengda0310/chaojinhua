var sfs;

//------------------------------------
// USER INTERFACE HANDLERS
//------------------------------------

		var uid="10001466";
    var serverId = 1;
	var index =1;
		
function onConnectBtClick()
{
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
}

function onDisconnectBtClick()
{
	// Log message
	history("Disconnecting...");

	// Disconnect
	sfs.disconnect();
}

//------------------------------------
// LOGGER EVENT HANDLERS
//------------------------------------

function onDebugLogged(event)
{
	history(event.message, "DEBUG", true);
}

function onInfoLogged(event)
{
	history(event.message, "INFO", true);
}

function onWarningLogged(event)
{
	history(event.message, "WARN", true);
}

function onErrorLogged(event)
{
	history(event.message, "ERROR", true);
}

//------------------------------------
// SFS EVENT HANDLERS
//------------------------------------

function onLogin(event)
{
		var send = new SFS2X.SFSObject();
		send.putUtfString("userId",uid);
		send.putInt("serverId", serverId);
		
		var req = new SFS2X.SFSObject();
		req.putSFSObject("infor", send);
		req.putInt("index", index++);
		
		sfs.send(new SFS2X.ExtensionRequest("1002",req));
}
function onLoginError(evtParams)
{
	history("Login failure: " + evtParams.errorMessage);
}
function onExtensionResponse(event)
{
	history(event.params.getUtfString("infor"));
	trace(event.params.getUtfString("infor"));
}
function onSendBtClick() {
    var cmd = document.getElementById("cmd").value;
    var txt = document.getElementById("param").value;
	
	var param = new SFS2X.SFSObject();
	param.putInt("index", index++);
	param.putUtfString("infor",txt);
	
	var req = new SFS2X.ExtensionRequest(cmd,param);
	history(cmd+":  "+txt);
	sfs.send(req);
}
function onConnection(event)
{
	if (event.success)
	{
		history("Connected to SmartFoxServer 2X!<br>SFS2X API version: " + sfs.version);

		// Show disconnect button
		switchButtons();
		try
		{
			var req = new SFS2X.SFSObject();
			req.putBool("isRepeat",false);
			sfs.send(new SFS2X.LoginRequest(uid, "111111", req, "server"));
		}
		catch(err)
		  {
			history(err);
		}
	} else
	{
		history("Connection failed: " + (event.errorMessage ? event.errorMessage + " (" + event.errorCode + ")" : "Is the server running at all?"));

		// Reset
		reset();
	}
}

function onConnectionLost(event)
{
	history("Disconnection occurred; reason is: " + event.reason);

	// Hide disconnect button
	switchButtons();

	// Reset
	reset();
}

//------------------------------------
// OTHER METHODS
//------------------------------------

function enableInterface(enabled)
{
	document.getElementById("addressIn").disabled = !enabled;
	document.getElementById("portIn").disabled = !enabled;
	document.getElementById("debugCb").disabled = !enabled;
	document.getElementById("connectBt").disabled = !enabled;
}

function trace(message)
{
		document.getElementById("json-target").innerHTML = new JSONFormat(message,4).toString();
		history(message);
}

function switchButtons()
{
    var connectBt = document.getElementById("connectBt");
    var disconnectBt = document.getElementById("disconnectBt");

    if (connectBt.style.display === "none")
    {
        connectBt.style.display = "block";
        disconnectBt.style.display = "none";
    }
    else
    {
        connectBt.style.display = "none";
        disconnectBt.style.display = "block";
    }
}

function reset()
{
	// Enable interface
	enableInterface(true);

	// Remove SFS2X listeners
	sfs.removeEventListener(SFS2X.SFSEvent.CONNECTION, onConnection);
	sfs.removeEventListener(SFS2X.SFSEvent.CONNECTION_LOST, onConnectionLost);
	
	sfs.logger.removeEventListener(SFS2X.LoggerEvent.DEBUG, onDebugLogged);
	sfs.logger.removeEventListener(SFS2X.LoggerEvent.INFO, onInfoLogged);
	sfs.logger.removeEventListener(SFS2X.LoggerEvent.WARNING, onWarningLogged);
	sfs.logger.removeEventListener(SFS2X.LoggerEvent.ERROR, onErrorLogged);
	
	sfs = null;
}

function history(msg) {
	$("#json-src").val($("#json-src").val()+"\r\n\r\n"+msg);
}


