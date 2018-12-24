/*
 * This file launches the application by asking Ext JS to create
 * and launch() the Application class.
 */
Ext.application({
    extend: 'web.Application',

    name: 'web',

    requires: [
        // This will automatically load all classes in the web namespace
        // so that application classes do not need to require each other.
        'web.*'
    ],

    // The name of the initial view to create.
    mainView: 'web.view.main.Main'
});
