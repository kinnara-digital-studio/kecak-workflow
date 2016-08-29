<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <title>@@enterprise.console.header.top.title@@</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="${request.contextPath}/images/favicon.ico"/>
        <link rel="stylesheet" type="text/css" href="${request.contextPath}/js/jquery/themes/ui-lightness/jquery-ui-1.10.3.custom.css"/>
        <link rel="stylesheet" type="text/css" href="${request.contextPath}/js/font-awesome/css/font-awesome.min.css"/>
        <link rel="stylesheet" href="${request.contextPath}/css/v3.css"/>
        <link rel="stylesheet" type="text/css" href="${request.contextPath}/css/v5.css"/>
        <link rel="stylesheet" type="text/css" href="${request.contextPath}/css/console_custom.css"/>
        <#if close??>
            <script type="text/javascript">
                location.href="${request.contextPath}/web/console/app/${appId}/${version}/processes";
            </script>
        </#if>
        <script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-1.9.1.min.js"></script>
        <script type="text/javascript" src="${request.contextPath}/js/jquery/jquery-migrate-1.2.1.min.js"></script>
        <script type="text/javascript" src="${request.contextPath}/js/jquery/ui/jquery-ui-1.10.3.min.js"></script>
        <script type="text/javascript" src="${request.contextPath}/js/jquery/flexigrid/flexigrid.js"></script>
        <script type="text/javascript" src="${request.contextPath}/js/json/ui.js?build=2094"></script>
        <script type="text/javascript" src="${request.contextPath}/js/json/ui_ext.js?build=2094"></script>
        <script type="text/javascript" src="${request.contextPath}/js/json/util.js?build=2094"></script>
    </head>
    <body>
        <div id="main-header">
            <a id="home-link" href="${request.contextPath}">
                <span id="logo"></span>
                <span id="logo-title">@@enterprise.console.header.top.title@@</span>
            </a>
        </div>
        <div id="container">
            <div id="content-container">
                <div id="nav">
                    <div id="nav-title">
                        <p><span id="appName" class="nav-title">${appDef.name}</span></p>
                        <p><span id="appName" class="nav-subtitle">${appInfo}</p>
                        <p id="nav-links">
                            <a href="#" onclick="version()" class="smallbutton">@@console.app.common.label.version@@ ${version}</a> 
                            <a href="#" onclick="version()" class="smallbutton"><#if appDef.published>@@console.app.common.label.published@@</#if><#if !appDef.published>@@console.app.common.label.notPublished@@</#if></a>
                        </p>
                        <script>
                            UI.base = "${request.contextPath}";
                            var versionDialog = new PopupDialog("${request.contextPath}/web/console/app/${appId}/versioning", " ");
                            function version(){
                                versionDialog.init();
                            }
                        </script>
                    </div>
                    <div id="nav-body">
                        <ul id="nav-list">
                            <li id="nav-app-forms"><a class="nav-link" href="${request.contextPath}/web/console/app/${appId}/${version}/forms"><span class="nav-steps">1</span> @@console.header.submenu.label.formsAndUi@@</a></li>
                            <li id="nav-app-processes"><a class="nav-link" href="${request.contextPath}/web/console/app/${appId}/${version}/processes"><span class="nav-steps">2</span> @@console.header.submenu.label.processes@@</a></li>
                            <li id="nav-app-props"><a class="nav-link" href="${request.contextPath}/web/console/app/${appId}/${version}/properties"><span class="nav-steps">3</span> @@console.header.submenu.label.properties@@</a></li>
                            <li id="nav-apps-link"><a class="nav-link" href="${request.contextPath}/web/desktop/apps"><i class="icon-circle-arrow-left" style="font-size: 20px; width: 17px; margin-right: 6px;"></i> @@console.header.submenu.label.allApps@@</a></li>
                        </ul>
                    </div>
                </div>
                <div id="main">
                    <div id="main-title"></div>
                    <div id="main-action">
                        <ul id="main-action-buttons">
                        </ul>
                    </div>
                    <div id="main-body">
                        <form id="appLicenseForm" class="form" method="POST" action="service">
                            <fieldset>
                                <legend>@@enterprise.console.label.appLicense@@</legend>
                                <#if message?? && message! != ''>
                                    <div style="text-align:center" class="form-errors">
                                        ${message!}
                                    </div>    
                                </#if>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.license.appId@@</label>
                                    <span class="form-input">${appId!}</span>
                                </div>
                                <#if licensor?? && licensor! != ''>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.license.licensor@@</label>
                                    <span class="form-input">${licensor!}</span>
                                </div>
                                </#if>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.license.holder@@</label>
                                    <span class="form-input">${holder!}</span>
                                </div>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.license.systemKey@@</label>
                                    <span class="form-input">${systemKey!}</span>
                                </div>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.username@@</label>
                                    <span class="form-input"><input id="username" name="username" type="text" value=""/> *</span>
                                </div>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.password@@</label>
                                    <span class="form-input"><input id="password" name="password" type="password" value=""/> *</span>
                                </div>
                                <div class="form-row">
                                    <label for="field1">@@enterprise.console.label.appLicense@@</label>
                                    <span class="form-input"><textarea id="license" name="license" cols="40" rows="10"></textarea> *</span>
                                </div>
                            </fieldset>
                            <div class="form-buttons">
                                <input class="form-button" type="submit" value="@@enterprise.console.label.submit@@" />
                                <input type="hidden" name="appId" value="${appId!}" />
                                <input type="hidden" name="version" value="${version!}" />
                                <input type="hidden" name="spot" value="appLicense" />
                                <input type="hidden" name="action" value="submit" />
                            </div>
                        </form>
                        <p>&nbsp;</p>
                        <p>&nbsp;</p>
                    </div>
                </div>
            </div>
        </div>
        <script>
            if (window.parent !== self && window.parent.name !== "quickOverlayFrame") {
                $("#main-header, #main-menu, #header, #footer, #adminBar, #beta").hide();
                $("#container, #nav, #menu-popup").css("top", "0px");
            } else {
                $("#main-header, #header, #footer, #adminBar, #beta").show();
            }
        </script>
        <script src="${request.contextPath}/csrf"></script>
    </body>
</html>
