/*
 Copyright (C) 2014 Typesafe, Inc <http://typesafe.com>
 */
define([
  "main/plugins",
  "services/sbt",
  "services/inspect/connection",
  'services/inspect/runPluginHandler',
  "widgets/layout/layout",
  "text!./run.html",
  "css!./run",
  "css!widgets/buttons/switch",
  "css!widgets/buttons/button",
  "css!widgets/menu/menu",
  "css!widgets/buttons/select"
], function(
  plugins,
  sbt,
  connection,
  runPluginHandler,
  layout,
  tpl
) {

  var subplugin = ko.observable();
  var currentPlugin;
  var inspects = ko.observable();
  var sbtExecCommand = function(cmd){
    sbt.tasks.requestExecution(cmd);
  }
  var mainRunAction = function() {
    if (sbt.tasks.pendingTasks.run()){
      sbt.tasks.actions.kill("run");
    } else {
      runPluginHandler.running();
      sbt.tasks.actions.run();
    }
  }
  var mainRunName = ko.computed(function() {
    return sbt.tasks.pendingTasks.run()?"Stop":"Run";
  });

  sbt.app.inspectorActivated.subscribe(function(active) {
    if (!active && window.location.hash.indexOf("#run/system") != 0) {
      window.location.hash = "run/system";
    } else {
      runPluginHandler.running();
    }
  })

  var State = {
    subplugin: subplugin,
    sbtExecCommand: sbtExecCommand,
    inspects: inspects,
    sbt: sbt,
    rerunOnBuild: sbt.app.settings.rerunOnBuild,
    automaticResetInspect: sbt.app.settings.automaticResetInspect,
    showLogDebug: sbt.app.settings.showLogDebug,
    inspectorActivated: sbt.app.inspectorActivated,
    mainRunAction: mainRunAction,
    mainRunName: mainRunName
  }

  // Subplugins titles
  var subPlugins = {
    system:         "Stdout",
    actors:         "Actors",
    requests:       "Requests",
    deviations:     "Deviations"
  }

  return {
    render: function(url) {
      layout.renderPlugin(ko.bindhtml(tpl, State))
    },
    route: plugins.route('run', function(url, breadcrumb, plugin) {
      subplugin(plugin.render());
      currentPlugin = plugin;
      breadcrumb([['run/', "Run"],['run/'+url.parameters[0], subPlugins[url.parameters[0]]]]);
    }, "run/system"),

    keyboard: function(key, meta, e) {
      if (currentPlugin.keyboard) {
        currentPlugin.keyboard(key, meta, e);
      };
    }
  }
});
