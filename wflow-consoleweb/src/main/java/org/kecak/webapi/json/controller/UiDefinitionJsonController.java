package org.kecak.webapi.json.controller;

import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.dao.DatalistDefinitionDao;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AuthTokenService;
import org.joget.apps.datalist.service.DataListService;
import org.joget.workflow.model.service.WorkflowManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UiDefinitionJsonController {
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private AppService appService;
    @Autowired
    private AppDefinitionDao appDefinitionDao;
    @Autowired
    private DataListService dataListService;
    @Autowired
    private DatalistDefinitionDao datalistDefinitionDao;
    @Autowired
    AuthTokenService authTokenService;


}
