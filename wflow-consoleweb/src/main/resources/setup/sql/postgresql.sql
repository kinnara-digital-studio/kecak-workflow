-- Converted by db_converter
START TRANSACTION;
SET standard_conforming_strings=off;
SET escape_string_warning=off;
SET CONSTRAINTS ALL DEFERRED;

CREATE TABLE SHKActivities (
    Id varchar(200) NOT NULL,
    ActivitySetDefinitionId varchar(180) DEFAULT NULL,
    ActivityDefinitionId varchar(180) NOT NULL,
    Process decimal(19,0) NOT NULL,
    TheResource decimal(19,0) DEFAULT NULL,
    PDefName varchar(400) NOT NULL,
    ProcessId varchar(400) NOT NULL,
    ResourceId varchar(200) DEFAULT NULL,
    State decimal(19,0) NOT NULL,
    BlockActivityId varchar(200) DEFAULT NULL,
    Performer varchar(200) DEFAULT NULL,
    IsPerformerAsynchronous boolean DEFAULT NULL,
    Priority integer DEFAULT NULL,
    Name varchar(508) DEFAULT NULL,
    Activated bigint NOT NULL,
    ActivatedTZO bigint NOT NULL,
    Accepted bigint DEFAULT NULL,
    AcceptedTZO bigint DEFAULT NULL,
    LastStateTime bigint NOT NULL,
    LastStateTimeTZO bigint NOT NULL,
    LimitTime bigint NOT NULL,
    LimitTimeTZO bigint NOT NULL,
    Description varchar(508) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (Id)
);

CREATE TABLE SHKActivityData (
    Activity decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValue bytea ,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL double precision DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    IsResult boolean NOT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (Activity,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKActivityDataBLOBs (
    ActivityDataWOB decimal(19,0) NOT NULL,
    VariableValue bytea ,
    OrdNo integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (ActivityDataWOB,OrdNo)
);

CREATE TABLE SHKActivityDataWOB (
    Activity decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL double precision DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    IsResult boolean NOT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (Activity,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKActivityStateEventAudits (
    KeyValue varchar(60) NOT NULL,
    Name varchar(100) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (KeyValue),
    UNIQUE (Name)
);

CREATE TABLE SHKActivityStates (
    KeyValue varchar(60) NOT NULL,
    Name varchar(100) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (KeyValue),
    UNIQUE (Name)
);

INSERT INTO SHKActivityStates VALUES ('open.running','open.running',1000001,0),('open.not_running.not_started','open.not_running.not_started',1000003,0),('open.not_running.suspended','open.not_running.suspended',1000005,0),('closed.completed','closed.completed',1000007,0),('closed.terminated','closed.terminated',1000009,0),('closed.aborted','closed.aborted',1000011,0);
CREATE TABLE SHKAndJoinTable (
    Process decimal(19,0) NOT NULL,
    BlockActivity decimal(19,0) DEFAULT NULL,
    ActivityDefinitionId varchar(180) NOT NULL,
    Activity decimal(19,0) NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKAssignmentEventAudits (
    RecordedTime bigint NOT NULL,
    RecordedTimeTZO bigint NOT NULL,
    TheUsername varchar(200) NOT NULL,
    TheType decimal(19,0) NOT NULL,
    ActivityId varchar(200) NOT NULL,
    ActivityName varchar(508) DEFAULT NULL,
    ProcessId varchar(200) NOT NULL,
    ProcessName varchar(508) DEFAULT NULL,
    ProcessFactoryName varchar(400) NOT NULL,
    ProcessFactoryVersion varchar(40) NOT NULL,
    ActivityDefinitionId varchar(180) NOT NULL,
    ActivityDefinitionName varchar(180) DEFAULT NULL,
    ActivityDefinitionType integer NOT NULL,
    ProcessDefinitionId varchar(180) NOT NULL,
    ProcessDefinitionName varchar(180) DEFAULT NULL,
    PackageId varchar(180) NOT NULL,
    OldResourceUsername varchar(200) DEFAULT NULL,
    OldResourceName varchar(200) DEFAULT NULL,
    NewResourceUsername varchar(200) NOT NULL,
    NewResourceName varchar(200) DEFAULT NULL,
    IsAccepted boolean NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKAssignmentsTable (
    Activity decimal(19,0) NOT NULL,
    TheResource decimal(19,0) NOT NULL,
    ActivityId varchar(200) NOT NULL,
    ActivityProcessId varchar(200) NOT NULL,
    ActivityProcessDefName varchar(400) NOT NULL,
    ResourceId varchar(200) NOT NULL,
    IsAccepted boolean NOT NULL,
    IsValid boolean NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (Activity,TheResource)
);

CREATE TABLE SHKCounters (
    name varchar(200) NOT NULL,
    the_number decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (name)
);

CREATE TABLE SHKCreateProcessEventAudits (
    RecordedTime bigint NOT NULL,
    RecordedTimeTZO bigint NOT NULL,
    TheUsername varchar(200) NOT NULL,
    TheType decimal(19,0) NOT NULL,
    ProcessId varchar(200) NOT NULL,
    ProcessName varchar(508) DEFAULT NULL,
    ProcessFactoryName varchar(400) NOT NULL,
    ProcessFactoryVersion varchar(40) NOT NULL,
    ProcessDefinitionId varchar(180) NOT NULL,
    ProcessDefinitionName varchar(180) DEFAULT NULL,
    PackageId varchar(180) NOT NULL,
    PActivityId varchar(200) DEFAULT NULL,
    PProcessId varchar(200) DEFAULT NULL,
    PProcessName varchar(508) DEFAULT NULL,
    PProcessFactoryName varchar(400) DEFAULT NULL,
    PProcessFactoryVersion varchar(40) DEFAULT NULL,
    PActivityDefinitionId varchar(180) DEFAULT NULL,
    PActivityDefinitionName varchar(180) DEFAULT NULL,
    PProcessDefinitionId varchar(180) DEFAULT NULL,
    PProcessDefinitionName varchar(180) DEFAULT NULL,
    PPackageId varchar(180) DEFAULT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKDataEventAudits (
    RecordedTime bigint NOT NULL,
    RecordedTimeTZO bigint NOT NULL,
    TheUsername varchar(200) NOT NULL,
    TheType decimal(19,0) NOT NULL,
    ActivityId varchar(200) DEFAULT NULL,
    ActivityName varchar(508) DEFAULT NULL,
    ProcessId varchar(200) NOT NULL,
    ProcessName varchar(508) DEFAULT NULL,
    ProcessFactoryName varchar(400) NOT NULL,
    ProcessFactoryVersion varchar(40) NOT NULL,
    ActivityDefinitionId varchar(180) DEFAULT NULL,
    ActivityDefinitionName varchar(180) DEFAULT NULL,
    ActivityDefinitionType integer DEFAULT NULL,
    ProcessDefinitionId varchar(180) NOT NULL,
    ProcessDefinitionName varchar(180) DEFAULT NULL,
    PackageId varchar(180) NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKDeadlines (
    Process decimal(19,0) NOT NULL,
    Activity decimal(19,0) NOT NULL,
    CNT decimal(19,0) NOT NULL,
    TimeLimit bigint NOT NULL,
    TimeLimitTZO bigint NOT NULL,
    ExceptionName varchar(200) NOT NULL,
    IsSynchronous boolean NOT NULL,
    IsExecuted boolean NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKEventTypes (
    KeyValue varchar(60) NOT NULL,
    Name varchar(100) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (KeyValue),
    UNIQUE (Name)
);

CREATE TABLE SHKGroupGroupTable (
    sub_gid decimal(19,0) NOT NULL,
    groupid decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (sub_gid,groupid)
);

CREATE TABLE SHKGroupTable (
    groupid varchar(200) NOT NULL,
    description varchar(508) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (groupid)
);

CREATE TABLE SHKGroupUser (
    USERNAME varchar(200) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (USERNAME)
);

CREATE TABLE SHKGroupUserPackLevelPart (
    PARTICIPANTOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANTOID,USEROID)
);

CREATE TABLE SHKGroupUserProcLevelPart (
    PARTICIPANTOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANTOID,USEROID)
);

CREATE TABLE SHKNewEventAuditData (
    DataEventAudit decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValue bytea ,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL float DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (DataEventAudit,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKNewEventAuditDataBLOBs (
    NewEventAuditDataWOB decimal(19,0) NOT NULL,
    VariableValue bytea ,
    OrdNo integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (NewEventAuditDataWOB,OrdNo)
);

CREATE TABLE SHKNewEventAuditDataWOB (
    DataEventAudit decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL float DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (DataEventAudit,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKNextXPDLVersions (
    XPDLId varchar(180) NOT NULL,
    NextVersion varchar(40) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDLId,NextVersion)
);

CREATE TABLE SHKNormalUser (
    USERNAME varchar(200) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (USERNAME)
);

CREATE TABLE SHKOldEventAuditData (
    DataEventAudit decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValue bytea ,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL float DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (DataEventAudit,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKOldEventAuditDataBLOBs (
    OldEventAuditDataWOB decimal(19,0) NOT NULL,
    VariableValue bytea ,
    OrdNo integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (OldEventAuditDataWOB,OrdNo)
);

CREATE TABLE SHKOldEventAuditDataWOB (
    DataEventAudit decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL float DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (DataEventAudit,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKPackLevelParticipant (
    PARTICIPANT_ID varchar(180) NOT NULL,
    PACKAGEOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANT_ID,PACKAGEOID)
);

CREATE TABLE SHKPackLevelXPDLApp (
    APPLICATION_ID varchar(180) NOT NULL,
    PACKAGEOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (APPLICATION_ID,PACKAGEOID)
);

CREATE TABLE SHKPackLevelXPDLAppTAAppDetUsr (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKPackLevelXPDLAppTAAppDetail (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKPackLevelXPDLAppTAAppUser (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKPackLevelXPDLAppToolAgntApp (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKProcLevelParticipant (
    PARTICIPANT_ID varchar(180) NOT NULL,
    PROCESSOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANT_ID,PROCESSOID)
);

CREATE TABLE SHKProcLevelXPDLApp (
    APPLICATION_ID varchar(180) NOT NULL,
    PROCESSOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (APPLICATION_ID,PROCESSOID)
);

CREATE TABLE SHKProcLevelXPDLAppTAAppDetUsr (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKProcLevelXPDLAppTAAppDetail (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKProcLevelXPDLAppTAAppUser (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKProcLevelXPDLAppToolAgntApp (
    XPDL_APPOID decimal(19,0) NOT NULL,
    TOOLAGENTOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDL_APPOID,TOOLAGENTOID)
);

CREATE TABLE SHKProcessData (
    Process decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValue bytea ,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL double precision DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (Process,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKProcessDataBLOBs (
    ProcessDataWOB decimal(19,0) NOT NULL,
    VariableValue bytea ,
    OrdNo integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (ProcessDataWOB,OrdNo)
);

CREATE TABLE SHKProcessDataWOB (
    Process decimal(19,0) NOT NULL,
    VariableDefinitionId varchar(200) NOT NULL,
    VariableType integer NOT NULL,
    VariableValueXML text ,
    VariableValueVCHAR varchar(8000) DEFAULT NULL,
    VariableValueDBL double precision DEFAULT NULL,
    VariableValueLONG bigint DEFAULT NULL,
    VariableValueDATE timestamp with time zone DEFAULT NULL,
    VariableValueBOOL boolean DEFAULT NULL,
    OrdNo integer NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (Process,VariableDefinitionId,OrdNo)
);

CREATE TABLE SHKProcessDefinitions (
    Name varchar(400) NOT NULL,
    PackageId varchar(180) NOT NULL,
    ProcessDefinitionId varchar(180) NOT NULL,
    ProcessDefinitionCreated bigint NOT NULL,
    ProcessDefinitionVersion varchar(40) NOT NULL,
    State integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (Name)
);

CREATE TABLE SHKProcessRequesters (
    Id varchar(200) NOT NULL,
    ActivityRequester decimal(19,0) DEFAULT NULL,
    ResourceRequester decimal(19,0) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (Id)
);

CREATE TABLE SHKProcessStateEventAudits (
    KeyValue varchar(60) NOT NULL,
    Name varchar(100) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (KeyValue),
    UNIQUE (Name)
);

CREATE TABLE SHKProcessStates (
    KeyValue varchar(60) NOT NULL,
    Name varchar(100) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (KeyValue),
    UNIQUE (Name)
);

INSERT INTO SHKProcessStates VALUES ('open.running','open.running',1000000,0),('open.not_running.not_started','open.not_running.not_started',1000002,0),('open.not_running.suspended','open.not_running.suspended',1000004,0),('closed.completed','closed.completed',1000006,0),('closed.terminated','closed.terminated',1000008,0),('closed.aborted','closed.aborted',1000010,0);
CREATE TABLE SHKProcesses (
    SyncVersion bigint NOT NULL,
    Id varchar(200) NOT NULL,
    ProcessDefinition decimal(19,0) NOT NULL,
    PDefName varchar(400) NOT NULL,
    ActivityRequesterId varchar(200) DEFAULT NULL,
    ActivityRequesterProcessId varchar(200) DEFAULT NULL,
    ResourceRequesterId varchar(200) NOT NULL,
    ExternalRequesterClassName varchar(508) DEFAULT NULL,
    State decimal(19,0) NOT NULL,
    Priority integer DEFAULT NULL,
    Name varchar(508) DEFAULT NULL,
    Created bigint NOT NULL,
    CreatedTZO bigint NOT NULL,
    Started bigint DEFAULT NULL,
    StartedTZO bigint DEFAULT NULL,
    LastStateTime bigint NOT NULL,
    LastStateTimeTZO bigint NOT NULL,
    LimitTime bigint NOT NULL,
    LimitTimeTZO bigint NOT NULL,
    Description varchar(508) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (Id)
);

CREATE TABLE SHKResourcesTable (
    Username varchar(200) NOT NULL,
    Name varchar(200) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (Username)
);

CREATE TABLE SHKStateEventAudits (
    RecordedTime bigint NOT NULL,
    RecordedTimeTZO bigint NOT NULL,
    TheUsername varchar(200) NOT NULL,
    TheType decimal(19,0) NOT NULL,
    ActivityId varchar(200) DEFAULT NULL,
    ActivityName varchar(508) DEFAULT NULL,
    ProcessId varchar(200) NOT NULL,
    ProcessName varchar(508) DEFAULT NULL,
    ProcessFactoryName varchar(400) NOT NULL,
    ProcessFactoryVersion varchar(40) NOT NULL,
    ActivityDefinitionId varchar(180) DEFAULT NULL,
    ActivityDefinitionName varchar(180) DEFAULT NULL,
    ActivityDefinitionType integer DEFAULT NULL,
    ProcessDefinitionId varchar(180) NOT NULL,
    ProcessDefinitionName varchar(180) DEFAULT NULL,
    PackageId varchar(180) NOT NULL,
    OldProcessState decimal(19,0) DEFAULT NULL,
    NewProcessState decimal(19,0) DEFAULT NULL,
    OldActivityState decimal(19,0) DEFAULT NULL,
    NewActivityState decimal(19,0) DEFAULT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKToolAgentApp (
    TOOL_AGENT_NAME varchar(500) NOT NULL,
    APP_NAME varchar(180) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (TOOL_AGENT_NAME,APP_NAME)
);

CREATE TABLE SHKToolAgentAppDetail (
    APP_MODE decimal(10,0) NOT NULL,
    TOOLAGENT_APPOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (APP_MODE,TOOLAGENT_APPOID)
);

CREATE TABLE SHKToolAgentAppDetailUser (
    TOOLAGENT_APPOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (TOOLAGENT_APPOID,USEROID)
);

CREATE TABLE SHKToolAgentAppUser (
    TOOLAGENT_APPOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (TOOLAGENT_APPOID,USEROID)
);

CREATE TABLE SHKToolAgentUser (
    USERNAME varchar(200) NOT NULL,
    PWD varchar(200) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (USERNAME)
);

CREATE TABLE SHKUserGroupTable (
    userid decimal(19,0) NOT NULL,
    groupid decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (userid,groupid)
);

CREATE TABLE SHKUserPackLevelPart (
    PARTICIPANTOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANTOID,USEROID)
);

CREATE TABLE SHKUserProcLevelParticipant (
    PARTICIPANTOID decimal(19,0) NOT NULL,
    USEROID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PARTICIPANTOID,USEROID)
);

CREATE TABLE SHKUserTable (
    userid varchar(200) NOT NULL,
    firstname varchar(100) DEFAULT NULL,
    lastname varchar(100) DEFAULT NULL,
    passwd varchar(100) NOT NULL,
    email varchar(508) DEFAULT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (userid)
);

CREATE TABLE SHKXPDLApplicationPackage (
    PACKAGE_ID varchar(180) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PACKAGE_ID)
);

CREATE TABLE SHKXPDLApplicationProcess (
    PROCESS_ID varchar(180) NOT NULL,
    PACKAGEOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PROCESS_ID,PACKAGEOID)
);

CREATE TABLE SHKXPDLData (
    XPDLContent bytea ,
    XPDLClassContent bytea ,
    XPDL decimal(19,0) NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT),
    UNIQUE (XPDL)
);

CREATE TABLE SHKXPDLHistory (
    XPDLId varchar(180) NOT NULL,
    XPDLVersion varchar(40) NOT NULL,
    XPDLClassVersion bigint NOT NULL,
    XPDLUploadTime timestamp with time zone NOT NULL,
    XPDLHistoryUploadTime timestamp with time zone NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDLId,XPDLVersion)
);

CREATE TABLE SHKXPDLHistoryData (
    XPDLContent bytea NOT NULL,
    XPDLClassContent bytea NOT NULL,
    XPDLHistory decimal(19,0) NOT NULL,
    CNT decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (CNT)
);

CREATE TABLE SHKXPDLParticipantPackage (
    PACKAGE_ID varchar(180) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PACKAGE_ID)
);

CREATE TABLE SHKXPDLParticipantProcess (
    PROCESS_ID varchar(180) NOT NULL,
    PACKAGEOID decimal(19,0) NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (PROCESS_ID,PACKAGEOID)
);

CREATE TABLE SHKXPDLReferences (
    ReferredXPDLId varchar(180) NOT NULL,
    ReferringXPDL decimal(19,0) NOT NULL,
    ReferredXPDLNumber integer NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (ReferredXPDLId,ReferringXPDL)
);

CREATE TABLE SHKXPDLS (
    XPDLId varchar(180) NOT NULL,
    XPDLVersion varchar(40) NOT NULL,
    XPDLClassVersion bigint NOT NULL,
    XPDLUploadTime timestamp with time zone NOT NULL,
    oid decimal(19,0) NOT NULL,
    version integer NOT NULL,
    PRIMARY KEY (oid),
    UNIQUE (XPDLId,XPDLVersion)
);

CREATE TABLE app_app (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    name varchar(510) DEFAULT NULL,
    published boolean DEFAULT NULL,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    license text ,
    PRIMARY KEY (appId,appVersion)
);

CREATE TABLE app_datalist (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description text ,
    json text ,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    PRIMARY KEY (appId,appVersion,id)
);

CREATE TABLE app_env_variable (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    id varchar(510) NOT NULL,
    value text ,
    remarks text ,
    PRIMARY KEY (appId,appVersion,id)
);

CREATE TABLE app_fd (
    id varchar(510) NOT NULL,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE app_form (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    formId varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    tableName varchar(510) DEFAULT NULL,
    json text ,
    PRIMARY KEY (appId,appVersion,formId)
);

CREATE TABLE app_message (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    ouid varchar(510) NOT NULL,
    messageKey varchar(510) DEFAULT NULL,
    locale varchar(510) DEFAULT NULL,
    message text ,
    PRIMARY KEY (appId,appVersion,ouid)
);

CREATE TABLE app_package (
    packageId varchar(510) NOT NULL,
    packageVersion bigint NOT NULL,
    name varchar(510) DEFAULT NULL,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    appId varchar(510) DEFAULT NULL,
    appVersion bigint DEFAULT NULL,
    PRIMARY KEY (packageId,packageVersion)
);

CREATE TABLE app_package_activity_form (
    processDefId varchar(510) NOT NULL,
    activityDefId varchar(510) NOT NULL,
    packageId varchar(510) NOT NULL,
    packageVersion bigint NOT NULL,
    ouid varchar(510) DEFAULT NULL,
    type varchar(510) DEFAULT NULL,
    formId varchar(510) DEFAULT NULL,
    formUrl varchar(510) DEFAULT NULL,
    formIFrameStyle varchar(510) DEFAULT NULL,
    autoContinue boolean DEFAULT NULL,
    PRIMARY KEY (processDefId,activityDefId,packageId,packageVersion)
);

CREATE TABLE app_package_activity_plugin (
    processDefId varchar(510) NOT NULL,
    activityDefId varchar(510) NOT NULL,
    packageId varchar(510) NOT NULL,
    packageVersion bigint NOT NULL,
    ouid varchar(510) DEFAULT NULL,
    pluginName varchar(510) DEFAULT NULL,
    pluginProperties text ,
    PRIMARY KEY (processDefId,activityDefId,packageId,packageVersion)
);

CREATE TABLE app_package_participant (
    processDefId varchar(510) NOT NULL,
    participantId varchar(510) NOT NULL,
    packageId varchar(510) NOT NULL,
    packageVersion bigint NOT NULL,
    ouid varchar(510) DEFAULT NULL,
    type varchar(510) DEFAULT NULL,
    value text ,
    pluginProperties text ,
    PRIMARY KEY (processDefId,participantId,packageId,packageVersion)
);

CREATE TABLE app_plugin_default (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    id varchar(510) NOT NULL,
    pluginName varchar(510) DEFAULT NULL,
    pluginDescription text ,
    pluginProperties text ,
    PRIMARY KEY (appId,appVersion,id)
);

CREATE TABLE app_report_activity (
    uuid varchar(510) NOT NULL,
    activityDefId varchar(510) DEFAULT NULL,
    activityName varchar(510) DEFAULT NULL,
    processUid varchar(510) DEFAULT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE app_report_activity_instance (
    instanceId varchar(510) NOT NULL,
    performer varchar(510) DEFAULT NULL,
    state varchar(510) DEFAULT NULL,
    status varchar(510) DEFAULT NULL,
    nameOfAcceptedUser varchar(510) DEFAULT NULL,
    assignmentUsers text ,
    due timestamp with time zone DEFAULT NULL,
    createdTime timestamp with time zone DEFAULT NULL,
    startedTime timestamp with time zone DEFAULT NULL,
    finishTime timestamp with time zone DEFAULT NULL,
    delay bigint DEFAULT NULL,
    timeConsumingFromCreatedTime bigint DEFAULT NULL,
    timeConsumingFromStartedTime bigint DEFAULT NULL,
    activityUid varchar(510) DEFAULT NULL,
    processInstanceId varchar(510) DEFAULT NULL,
    PRIMARY KEY (instanceId)
);

CREATE TABLE app_report_app (
    uuid varchar(510) NOT NULL,
    appId varchar(510) DEFAULT NULL,
    appVersion varchar(510) DEFAULT NULL,
    appName varchar(510) DEFAULT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE app_report_package (
    uuid varchar(510) NOT NULL,
    packageId varchar(510) DEFAULT NULL,
    packageName varchar(510) DEFAULT NULL,
    packageVersion varchar(510) DEFAULT NULL,
    appUid varchar(510) DEFAULT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE app_report_process (
    uuid varchar(510) NOT NULL,
    processDefId varchar(510) DEFAULT NULL,
    processName varchar(510) DEFAULT NULL,
    packageUid varchar(510) DEFAULT NULL,
    PRIMARY KEY (uuid)
);

CREATE TABLE app_report_process_instance (
    instanceId varchar(510) NOT NULL,
    requester varchar(510) DEFAULT NULL,
    state varchar(510) DEFAULT NULL,
    due timestamp with time zone DEFAULT NULL,
    startedTime timestamp with time zone DEFAULT NULL,
    finishTime timestamp with time zone DEFAULT NULL,
    delay bigint DEFAULT NULL,
    timeConsumingFromStartedTime bigint DEFAULT NULL,
    processUid varchar(510) DEFAULT NULL,
    PRIMARY KEY (instanceId)
);

CREATE TABLE app_userview (
    appId varchar(510) NOT NULL,
    appVersion bigint NOT NULL,
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description text ,
    json text ,
    dateCreated timestamp with time zone DEFAULT NULL,
    dateModified timestamp with time zone DEFAULT NULL,
    PRIMARY KEY (appId,appVersion,id)
);

CREATE TABLE dir_department (
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    organizationId varchar(510) DEFAULT NULL,
    hod varchar(510) DEFAULT NULL,
    parentId varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_employment (
    id varchar(510) NOT NULL,
    userId varchar(510) DEFAULT NULL,
    startDate date DEFAULT NULL,
    endDate date DEFAULT NULL,
    employeeCode varchar(510) DEFAULT NULL,
    role varchar(510) DEFAULT NULL,
    gradeId varchar(510) DEFAULT NULL,
    departmentId varchar(510) DEFAULT NULL,
    organizationId varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_employment_report_to (
    employmentId varchar(510) NOT NULL,
    reportToId varchar(510) NOT NULL,
    id varchar(510) DEFAULT NULL,
    PRIMARY KEY (employmentId,reportToId)
);

CREATE TABLE dir_grade (
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    organizationId varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_group (
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    organizationId varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_organization (
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    parentId varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_role (
    id varchar(510) NOT NULL,
    name varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

INSERT INTO dir_role VALUES ('ROLE_ADMIN','Admin','Administrator'),('ROLE_USER','User','Normal User'),
	('ROLE_MANAGER', 'Manager', 'Manager Apps'),('ROLE_MONITORING', 'Monitoring', 'Monitoring Apps');

CREATE TABLE dir_user (
    id varchar(510) NOT NULL,
    username varchar(510) DEFAULT NULL,
    password varchar(510) DEFAULT NULL,
    firstName varchar(510) DEFAULT NULL,
    lastName varchar(510) DEFAULT NULL,
    email varchar(510) DEFAULT NULL,
    active integer DEFAULT NULL,
    timeZone varchar(510) DEFAULT NULL,
    locale varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

INSERT INTO dir_user VALUES ('admin','admin','D475DD2BBF4138BB17263A3537B2F101','Admin','Admin',NULL,1,'0',NULL);

CREATE TABLE DIR_USER_SALT (
   	id varchar(510) NOT NULL,
	userid varchar(510) NOT NULL,
	randomsalt varchar(510) NOT NULL,
	PRIMARY KEY (id)
);

Insert into DIR_USER_SALT (id,userid,randomsalt) values ('09f1122b-a389-4a24-b245-c6102109d4c1','admin','2DEFB4535E58F417C62E632D82402986');
 
CREATE TABLE dir_user_extra (
    username varchar(510) NOT NULL,
    algorithm varchar(510) DEFAULT NULL,
    loginAttempt integer DEFAULT NULL,
    failedloginAttempt integer DEFAULT NULL,
    lastLogedInDate timestamp with time zone DEFAULT NULL,
    lockOutDate timestamp with time zone DEFAULT NULL,
    lastPasswordChangeDate timestamp with time zone DEFAULT NULL,
    requiredPasswordChange boolean DEFAULT NULL,
    noPasswordExpiration boolean DEFAULT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE dir_user_group (
    groupId varchar(510) NOT NULL,
    userId varchar(510) NOT NULL,
    PRIMARY KEY (userId,groupId)
);

CREATE TABLE dir_user_password_history (
    id varchar(510) NOT NULL,
    username varchar(510) DEFAULT NULL,
    salt varchar(510) DEFAULT NULL,
    password varchar(510) DEFAULT NULL,
    updatedDate timestamp with time zone DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE dir_user_role (
    roleId varchar(510) NOT NULL,
    userId varchar(510) NOT NULL,
    PRIMARY KEY (userId,roleId)
);

INSERT INTO dir_user_role VALUES ('ROLE_ADMIN','admin');
CREATE TABLE objectid (
    nextoid decimal(19,0) NOT NULL,
    PRIMARY KEY (nextoid)
);

INSERT INTO "objectid" VALUES (1000200);
CREATE TABLE wf_audit_trail (
    id varchar(510) NOT NULL,
    username varchar(510) DEFAULT NULL,
    clazz varchar(510) DEFAULT NULL,
    method varchar(510) DEFAULT NULL,
    message text ,
    timestamp timestamp with time zone DEFAULT NULL,
    appId varchar(510) DEFAULT NULL,
    appVersion varchar(510) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE wf_process_link (
    processId varchar(510) NOT NULL,
    parentProcessId varchar(510) DEFAULT NULL,
    originProcessId varchar(510) DEFAULT NULL,
    PRIMARY KEY (processId)
);

CREATE TABLE wf_report (
    activityInstanceId varchar(510) NOT NULL,
    processInstanceId varchar(510) DEFAULT NULL,
    priority varchar(510) DEFAULT NULL,
    createdTime timestamp with time zone DEFAULT NULL,
    startedTime timestamp with time zone DEFAULT NULL,
    dateLimit bigint DEFAULT NULL,
    due timestamp with time zone DEFAULT NULL,
    delay bigint DEFAULT NULL,
    finishTime timestamp with time zone DEFAULT NULL,
    timeConsumingFromDateCreated bigint DEFAULT NULL,
    timeConsumingFromDateStarted bigint DEFAULT NULL,
    performer varchar(510) DEFAULT NULL,
    nameOfAcceptedUser varchar(510) DEFAULT NULL,
    status varchar(510) DEFAULT NULL,
    state varchar(510) DEFAULT NULL,
    packageId varchar(510) DEFAULT NULL,
    processDefId varchar(510) DEFAULT NULL,
    activityDefId varchar(510) DEFAULT NULL,
    assignmentUsers text ,
    appId varchar(510) DEFAULT NULL,
    appVersion bigint DEFAULT NULL,
    PRIMARY KEY (activityInstanceId)
);

CREATE TABLE wf_report_activity (
    activityDefId varchar(510) NOT NULL,
    activityName varchar(510) DEFAULT NULL,
    description varchar(510) DEFAULT NULL,
    priority varchar(510) DEFAULT NULL,
    PRIMARY KEY (activityDefId)
);

CREATE TABLE wf_report_package (
    packageId varchar(510) NOT NULL,
    packageName varchar(510) DEFAULT NULL,
    PRIMARY KEY (packageId)
);

CREATE TABLE wf_report_process (
    processDefId varchar(510) NOT NULL,
    processName varchar(510) DEFAULT NULL,
    version varchar(510) DEFAULT NULL,
    PRIMARY KEY (processDefId)
);

CREATE TABLE wf_resource_bundle_message (
    id varchar(510) NOT NULL,
    messageKey varchar(510) DEFAULT NULL,
    locale varchar(510) DEFAULT NULL,
    message text ,
    PRIMARY KEY (id)
);

CREATE TABLE wf_setup (
    id varchar(510) NOT NULL,
    property varchar(510) DEFAULT NULL,
    value text ,
    ordering integer DEFAULT NULL,
    PRIMARY KEY (id)
);

-- Post-data save --
COMMIT;
START TRANSACTION;

-- Typecasts --

-- Foreign keys --
ALTER TABLE SHKActivities ADD CONSTRAINT SHKActivities_Process FOREIGN KEY (Process) REFERENCES SHKProcesses (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivities (Process);
ALTER TABLE SHKActivities ADD CONSTRAINT SHKActivities_State FOREIGN KEY (State) REFERENCES SHKActivityStates (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivities (State);
ALTER TABLE SHKActivities ADD CONSTRAINT SHKActivities_TheResource FOREIGN KEY (TheResource) REFERENCES SHKResourcesTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivities (TheResource);
ALTER TABLE SHKActivityData ADD CONSTRAINT SHKActivityData_Activity FOREIGN KEY (Activity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivityData (Activity);
ALTER TABLE SHKActivityDataBLOBs ADD CONSTRAINT SHKActivityDataBLOBs_ActivityDataWOB FOREIGN KEY (ActivityDataWOB) REFERENCES SHKActivityDataWOB (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivityDataBLOBs (ActivityDataWOB);
ALTER TABLE SHKActivityDataWOB ADD CONSTRAINT SHKActivityDataWOB_Activity FOREIGN KEY (Activity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKActivityDataWOB (Activity);
ALTER TABLE SHKAndJoinTable ADD CONSTRAINT SHKAndJoinTable_Activity FOREIGN KEY (Activity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAndJoinTable (Activity);
ALTER TABLE SHKAndJoinTable ADD CONSTRAINT SHKAndJoinTable_BlockActivity FOREIGN KEY (BlockActivity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAndJoinTable (BlockActivity);
ALTER TABLE SHKAndJoinTable ADD CONSTRAINT SHKAndJoinTable_Process FOREIGN KEY (Process) REFERENCES SHKProcesses (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAndJoinTable (Process);
ALTER TABLE SHKAssignmentEventAudits ADD CONSTRAINT SHKAssignmentEventAudits_TheType FOREIGN KEY (TheType) REFERENCES SHKEventTypes (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAssignmentEventAudits (TheType);
ALTER TABLE SHKAssignmentsTable ADD CONSTRAINT SHKAssignmentsTable_Activity FOREIGN KEY (Activity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAssignmentsTable (Activity);
ALTER TABLE SHKAssignmentsTable ADD CONSTRAINT SHKAssignmentsTable_TheResource FOREIGN KEY (TheResource) REFERENCES SHKResourcesTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKAssignmentsTable (TheResource);
ALTER TABLE SHKCreateProcessEventAudits ADD CONSTRAINT SHKCreateProcessEventAudits_TheType FOREIGN KEY (TheType) REFERENCES SHKEventTypes (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKCreateProcessEventAudits (TheType);
ALTER TABLE SHKDataEventAudits ADD CONSTRAINT SHKDataEventAudits_TheType FOREIGN KEY (TheType) REFERENCES SHKEventTypes (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKDataEventAudits (TheType);
ALTER TABLE SHKDeadlines ADD CONSTRAINT SHKDeadlines_Activity FOREIGN KEY (Activity) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKDeadlines (Activity);
ALTER TABLE SHKDeadlines ADD CONSTRAINT SHKDeadlines_Process FOREIGN KEY (Process) REFERENCES SHKProcesses (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKDeadlines (Process);
ALTER TABLE SHKGroupGroupTable ADD CONSTRAINT SHKGroupGroupTable_groupid FOREIGN KEY (groupid) REFERENCES SHKGroupTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupGroupTable (groupid);
ALTER TABLE SHKGroupGroupTable ADD CONSTRAINT SHKGroupGroupTable_sub_gid FOREIGN KEY (sub_gid) REFERENCES SHKGroupTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupGroupTable (sub_gid);
ALTER TABLE SHKGroupUserPackLevelPart ADD CONSTRAINT SHKGroupUserPackLevelPart_PARTICIPANTOID FOREIGN KEY (PARTICIPANTOID) REFERENCES SHKPackLevelParticipant (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupUserPackLevelPart (PARTICIPANTOID);
ALTER TABLE SHKGroupUserPackLevelPart ADD CONSTRAINT SHKGroupUserPackLevelPart_USEROID FOREIGN KEY (USEROID) REFERENCES SHKGroupUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupUserPackLevelPart (USEROID);
ALTER TABLE SHKGroupUserProcLevelPart ADD CONSTRAINT SHKGroupUserProcLevelPart_PARTICIPANTOID FOREIGN KEY (PARTICIPANTOID) REFERENCES SHKProcLevelParticipant (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupUserProcLevelPart (PARTICIPANTOID);
ALTER TABLE SHKGroupUserProcLevelPart ADD CONSTRAINT SHKGroupUserProcLevelPart_USEROID FOREIGN KEY (USEROID) REFERENCES SHKGroupUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKGroupUserProcLevelPart (USEROID);
ALTER TABLE SHKNewEventAuditData ADD CONSTRAINT SHKNewEventAuditData_DataEventAudit FOREIGN KEY (DataEventAudit) REFERENCES SHKDataEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKNewEventAuditData (DataEventAudit);
ALTER TABLE SHKNewEventAuditDataBLOBs ADD CONSTRAINT SHKNewEventAuditDataBLOBs_NewEventAuditDataWOB FOREIGN KEY (NewEventAuditDataWOB) REFERENCES SHKNewEventAuditDataWOB (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKNewEventAuditDataBLOBs (NewEventAuditDataWOB);
ALTER TABLE SHKNewEventAuditDataWOB ADD CONSTRAINT SHKNewEventAuditDataWOB_DataEventAudit FOREIGN KEY (DataEventAudit) REFERENCES SHKDataEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKNewEventAuditDataWOB (DataEventAudit);
ALTER TABLE SHKOldEventAuditData ADD CONSTRAINT SHKOldEventAuditData_DataEventAudit FOREIGN KEY (DataEventAudit) REFERENCES SHKDataEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKOldEventAuditData (DataEventAudit);
ALTER TABLE SHKOldEventAuditDataBLOBs ADD CONSTRAINT SHKOldEventAuditDataBLOBs_OldEventAuditDataWOB FOREIGN KEY (OldEventAuditDataWOB) REFERENCES SHKOldEventAuditDataWOB (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKOldEventAuditDataBLOBs (OldEventAuditDataWOB);
ALTER TABLE SHKOldEventAuditDataWOB ADD CONSTRAINT SHKOldEventAuditDataWOB_DataEventAudit FOREIGN KEY (DataEventAudit) REFERENCES SHKDataEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKOldEventAuditDataWOB (DataEventAudit);
ALTER TABLE SHKPackLevelParticipant ADD CONSTRAINT SHKPackLevelParticipant_PACKAGEOID FOREIGN KEY (PACKAGEOID) REFERENCES SHKXPDLParticipantPackage (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelParticipant (PACKAGEOID);
ALTER TABLE SHKPackLevelXPDLApp ADD CONSTRAINT SHKPackLevelXPDLApp_PACKAGEOID FOREIGN KEY (PACKAGEOID) REFERENCES SHKXPDLApplicationPackage (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLApp (PACKAGEOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppDetUsr ADD CONSTRAINT SHKPackLevelXPDLAppTAAppDetUsr_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppDetailUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppDetUsr (TOOLAGENTOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppDetUsr ADD CONSTRAINT SHKPackLevelXPDLAppTAAppDetUsr_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKPackLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppDetUsr (XPDL_APPOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppDetail ADD CONSTRAINT SHKPackLevelXPDLAppTAAppDetail_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppDetail (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppDetail (TOOLAGENTOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppDetail ADD CONSTRAINT SHKPackLevelXPDLAppTAAppDetail_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKPackLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppDetail (XPDL_APPOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppUser ADD CONSTRAINT SHKPackLevelXPDLAppTAAppUser_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppUser (TOOLAGENTOID);
ALTER TABLE SHKPackLevelXPDLAppTAAppUser ADD CONSTRAINT SHKPackLevelXPDLAppTAAppUser_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKPackLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppTAAppUser (XPDL_APPOID);
ALTER TABLE SHKPackLevelXPDLAppToolAgntApp ADD CONSTRAINT SHKPackLevelXPDLAppToolAgntApp_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppToolAgntApp (TOOLAGENTOID);
ALTER TABLE SHKPackLevelXPDLAppToolAgntApp ADD CONSTRAINT SHKPackLevelXPDLAppToolAgntApp_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKPackLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKPackLevelXPDLAppToolAgntApp (XPDL_APPOID);
ALTER TABLE SHKProcLevelParticipant ADD CONSTRAINT SHKProcLevelParticipant_PROCESSOID FOREIGN KEY (PROCESSOID) REFERENCES SHKXPDLParticipantProcess (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelParticipant (PROCESSOID);
ALTER TABLE SHKProcLevelXPDLApp ADD CONSTRAINT SHKProcLevelXPDLApp_PROCESSOID FOREIGN KEY (PROCESSOID) REFERENCES SHKXPDLApplicationProcess (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLApp (PROCESSOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppDetUsr ADD CONSTRAINT SHKProcLevelXPDLAppTAAppDetUsr_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppDetailUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppDetUsr (TOOLAGENTOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppDetUsr ADD CONSTRAINT SHKProcLevelXPDLAppTAAppDetUsr_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKProcLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppDetUsr (XPDL_APPOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppDetail ADD CONSTRAINT SHKProcLevelXPDLAppTAAppDetail_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppDetail (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppDetail (TOOLAGENTOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppDetail ADD CONSTRAINT SHKProcLevelXPDLAppTAAppDetail_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKProcLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppDetail (XPDL_APPOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppUser ADD CONSTRAINT SHKProcLevelXPDLAppTAAppUser_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentAppUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppUser (TOOLAGENTOID);
ALTER TABLE SHKProcLevelXPDLAppTAAppUser ADD CONSTRAINT SHKProcLevelXPDLAppTAAppUser_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKProcLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppTAAppUser (XPDL_APPOID);
ALTER TABLE SHKProcLevelXPDLAppToolAgntApp ADD CONSTRAINT SHKProcLevelXPDLAppToolAgntApp_TOOLAGENTOID FOREIGN KEY (TOOLAGENTOID) REFERENCES SHKToolAgentApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppToolAgntApp (TOOLAGENTOID);
ALTER TABLE SHKProcLevelXPDLAppToolAgntApp ADD CONSTRAINT SHKProcLevelXPDLAppToolAgntApp_XPDL_APPOID FOREIGN KEY (XPDL_APPOID) REFERENCES SHKProcLevelXPDLApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcLevelXPDLAppToolAgntApp (XPDL_APPOID);
ALTER TABLE SHKProcessData ADD CONSTRAINT SHKProcessData_Process FOREIGN KEY (Process) REFERENCES SHKProcesses (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcessData (Process);
ALTER TABLE SHKProcessDataBLOBs ADD CONSTRAINT SHKProcessDataBLOBs_ProcessDataWOB FOREIGN KEY (ProcessDataWOB) REFERENCES SHKProcessDataWOB (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcessDataBLOBs (ProcessDataWOB);
ALTER TABLE SHKProcessDataWOB ADD CONSTRAINT SHKProcessDataWOB_Process FOREIGN KEY (Process) REFERENCES SHKProcesses (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcessDataWOB (Process);
ALTER TABLE SHKProcessRequesters ADD CONSTRAINT SHKProcessRequesters_ActivityRequester FOREIGN KEY (ActivityRequester) REFERENCES SHKActivities (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcessRequesters (ActivityRequester);
ALTER TABLE SHKProcessRequesters ADD CONSTRAINT SHKProcessRequesters_ResourceRequester FOREIGN KEY (ResourceRequester) REFERENCES SHKResourcesTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcessRequesters (ResourceRequester);
ALTER TABLE SHKProcesses ADD CONSTRAINT SHKProcesses_ProcessDefinition FOREIGN KEY (ProcessDefinition) REFERENCES SHKProcessDefinitions (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcesses (ProcessDefinition);
ALTER TABLE SHKProcesses ADD CONSTRAINT SHKProcesses_State FOREIGN KEY (State) REFERENCES SHKProcessStates (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKProcesses (State);
ALTER TABLE SHKStateEventAudits ADD CONSTRAINT SHKStateEventAudits_NewActivityState FOREIGN KEY (NewActivityState) REFERENCES SHKActivityStateEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKStateEventAudits (NewActivityState);
ALTER TABLE SHKStateEventAudits ADD CONSTRAINT SHKStateEventAudits_NewProcessState FOREIGN KEY (NewProcessState) REFERENCES SHKProcessStateEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKStateEventAudits (NewProcessState);
ALTER TABLE SHKStateEventAudits ADD CONSTRAINT SHKStateEventAudits_OldActivityState FOREIGN KEY (OldActivityState) REFERENCES SHKActivityStateEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKStateEventAudits (OldActivityState);
ALTER TABLE SHKStateEventAudits ADD CONSTRAINT SHKStateEventAudits_OldProcessState FOREIGN KEY (OldProcessState) REFERENCES SHKProcessStateEventAudits (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKStateEventAudits (OldProcessState);
ALTER TABLE SHKStateEventAudits ADD CONSTRAINT SHKStateEventAudits_TheType FOREIGN KEY (TheType) REFERENCES SHKEventTypes (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKStateEventAudits (TheType);
ALTER TABLE SHKToolAgentAppDetail ADD CONSTRAINT SHKToolAgentAppDetail_TOOLAGENT_APPOID FOREIGN KEY (TOOLAGENT_APPOID) REFERENCES SHKToolAgentApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKToolAgentAppDetail (TOOLAGENT_APPOID);
ALTER TABLE SHKToolAgentAppDetailUser ADD CONSTRAINT SHKToolAgentAppDetailUser_TOOLAGENT_APPOID FOREIGN KEY (TOOLAGENT_APPOID) REFERENCES SHKToolAgentAppDetail (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKToolAgentAppDetailUser (TOOLAGENT_APPOID);
ALTER TABLE SHKToolAgentAppDetailUser ADD CONSTRAINT SHKToolAgentAppDetailUser_USEROID FOREIGN KEY (USEROID) REFERENCES SHKToolAgentUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKToolAgentAppDetailUser (USEROID);
ALTER TABLE SHKToolAgentAppUser ADD CONSTRAINT SHKToolAgentAppUser_TOOLAGENT_APPOID FOREIGN KEY (TOOLAGENT_APPOID) REFERENCES SHKToolAgentApp (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKToolAgentAppUser (TOOLAGENT_APPOID);
ALTER TABLE SHKToolAgentAppUser ADD CONSTRAINT SHKToolAgentAppUser_USEROID FOREIGN KEY (USEROID) REFERENCES SHKToolAgentUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKToolAgentAppUser (USEROID);
ALTER TABLE SHKUserGroupTable ADD CONSTRAINT SHKUserGroupTable_groupid FOREIGN KEY (groupid) REFERENCES SHKGroupTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserGroupTable (groupid);
ALTER TABLE SHKUserGroupTable ADD CONSTRAINT SHKUserGroupTable_userid FOREIGN KEY (userid) REFERENCES SHKUserTable (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserGroupTable (userid);
ALTER TABLE SHKUserPackLevelPart ADD CONSTRAINT SHKUserPackLevelPart_PARTICIPANTOID FOREIGN KEY (PARTICIPANTOID) REFERENCES SHKPackLevelParticipant (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserPackLevelPart (PARTICIPANTOID);
ALTER TABLE SHKUserPackLevelPart ADD CONSTRAINT SHKUserPackLevelPart_USEROID FOREIGN KEY (USEROID) REFERENCES SHKNormalUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserPackLevelPart (USEROID);
ALTER TABLE SHKUserProcLevelParticipant ADD CONSTRAINT SHKUserProcLevelParticipant_PARTICIPANTOID FOREIGN KEY (PARTICIPANTOID) REFERENCES SHKProcLevelParticipant (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserProcLevelParticipant (PARTICIPANTOID);
ALTER TABLE SHKUserProcLevelParticipant ADD CONSTRAINT SHKUserProcLevelParticipant_USEROID FOREIGN KEY (USEROID) REFERENCES SHKNormalUser (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKUserProcLevelParticipant (USEROID);
ALTER TABLE SHKXPDLApplicationProcess ADD CONSTRAINT SHKXPDLApplicationProcess_PACKAGEOID FOREIGN KEY (PACKAGEOID) REFERENCES SHKXPDLApplicationPackage (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKXPDLApplicationProcess (PACKAGEOID);
ALTER TABLE SHKXPDLData ADD CONSTRAINT SHKXPDLData_XPDL FOREIGN KEY (XPDL) REFERENCES SHKXPDLS (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKXPDLData (XPDL);
ALTER TABLE SHKXPDLHistoryData ADD CONSTRAINT SHKXPDLHistoryData_XPDLHistory FOREIGN KEY (XPDLHistory) REFERENCES SHKXPDLHistory (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKXPDLHistoryData (XPDLHistory);
ALTER TABLE SHKXPDLParticipantProcess ADD CONSTRAINT SHKXPDLParticipantProcess_PACKAGEOID FOREIGN KEY (PACKAGEOID) REFERENCES SHKXPDLParticipantPackage (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKXPDLParticipantProcess (PACKAGEOID);
ALTER TABLE SHKXPDLReferences ADD CONSTRAINT SHKXPDLReferences_ReferringXPDL FOREIGN KEY (ReferringXPDL) REFERENCES SHKXPDLS (oid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON SHKXPDLReferences (ReferringXPDL);
ALTER TABLE app_datalist ADD CONSTRAINT FK5E9247A6462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_datalist (appId, appVersion);
ALTER TABLE app_env_variable ADD CONSTRAINT FK740A62EC462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_env_variable (appId, appVersion);
ALTER TABLE app_form ADD CONSTRAINT FK45957822462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_form (appId, appVersion);
ALTER TABLE app_message ADD CONSTRAINT FKEE346FE9462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_message (appId, appVersion);
ALTER TABLE app_package ADD CONSTRAINT FK852EA428462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_package (appId, appVersion);
ALTER TABLE app_package_activity_form ADD CONSTRAINT FKA8D741D5F255BCC FOREIGN KEY (packageId, packageVersion) REFERENCES app_package (packageId, packageVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_package_activity_form (packageId, packageVersion);
ALTER TABLE app_package_activity_plugin ADD CONSTRAINT FKADE8644C5F255BCC FOREIGN KEY (packageId, packageVersion) REFERENCES app_package (packageId, packageVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_package_activity_plugin (packageId, packageVersion);
ALTER TABLE app_package_participant ADD CONSTRAINT FK6D7BF59C5F255BCC FOREIGN KEY (packageId, packageVersion) REFERENCES app_package (packageId, packageVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_package_participant (packageId, packageVersion);
ALTER TABLE app_plugin_default ADD CONSTRAINT FK7A835713462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_plugin_default (appId, appVersion);
ALTER TABLE app_report_activity ADD CONSTRAINT FK5E33D79C918F93D FOREIGN KEY (processUid) REFERENCES app_report_process (uuid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_activity (processUid);
ALTER TABLE app_report_activity_instance ADD CONSTRAINT FK9C6ABDD8D4610A90 FOREIGN KEY (processInstanceId) REFERENCES app_report_process_instance (instanceId) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_activity_instance (processInstanceId);
ALTER TABLE app_report_activity_instance ADD CONSTRAINT FK9C6ABDD8B06E2043 FOREIGN KEY (activityUid) REFERENCES app_report_activity (uuid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_activity_instance (activityUid);
ALTER TABLE app_report_package ADD CONSTRAINT FKBD580A19E475ABC FOREIGN KEY (appUid) REFERENCES app_report_app (uuid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_package (appUid);
ALTER TABLE app_report_process ADD CONSTRAINT FKDAFFF442D40695DD FOREIGN KEY (packageUid) REFERENCES app_report_package (uuid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_process (packageUid);
ALTER TABLE app_report_process_instance ADD CONSTRAINT FK351D7BF2918F93D FOREIGN KEY (processUid) REFERENCES app_report_process (uuid) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_report_process_instance (processUid);
ALTER TABLE app_userview ADD CONSTRAINT FKE411D54E462EF4C7 FOREIGN KEY (appId, appVersion) REFERENCES app_app (appId, appVersion) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON app_userview (appId, appVersion);
ALTER TABLE dir_department ADD CONSTRAINT FKEEE8AA4418CEBAE1 FOREIGN KEY (organizationId) REFERENCES dir_organization (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_department (organizationId);
ALTER TABLE dir_department ADD CONSTRAINT FKEEE8AA4480DB1449 FOREIGN KEY (hod) REFERENCES dir_employment (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_department (hod);
ALTER TABLE dir_department ADD CONSTRAINT FKEEE8AA44EF6BB2B7 FOREIGN KEY (parentId) REFERENCES dir_department (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_department (parentId);
ALTER TABLE dir_employment ADD CONSTRAINT FKC6620ADE18CEBAE1 FOREIGN KEY (organizationId) REFERENCES dir_organization (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment (organizationId);
ALTER TABLE dir_employment ADD CONSTRAINT FKC6620ADE14CE02E9 FOREIGN KEY (gradeId) REFERENCES dir_grade (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment (gradeId);
ALTER TABLE dir_employment ADD CONSTRAINT FKC6620ADE716AE35F FOREIGN KEY (departmentId) REFERENCES dir_department (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment (departmentId);
ALTER TABLE dir_employment ADD CONSTRAINT FKC6620ADECE539211 FOREIGN KEY (userId) REFERENCES dir_user (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment (userId);
ALTER TABLE dir_employment_report_to ADD CONSTRAINT FK536229452787E613 FOREIGN KEY (employmentId) REFERENCES dir_employment (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment_report_to (employmentId);
ALTER TABLE dir_employment_report_to ADD CONSTRAINT FK53622945F4068416 FOREIGN KEY (reportToId) REFERENCES dir_employment (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_employment_report_to (reportToId);
ALTER TABLE dir_grade ADD CONSTRAINT FKBC9A49A518CEBAE1 FOREIGN KEY (organizationId) REFERENCES dir_organization (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_grade (organizationId);
ALTER TABLE dir_group ADD CONSTRAINT FKBC9A804D18CEBAE1 FOREIGN KEY (organizationId) REFERENCES dir_organization (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_group (organizationId);
ALTER TABLE dir_organization ADD CONSTRAINT FK55A15FA5961BD498 FOREIGN KEY (parentId) REFERENCES dir_organization (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_organization (parentId);
ALTER TABLE dir_user_group ADD CONSTRAINT FK2F0367FD159B6639 FOREIGN KEY (groupId) REFERENCES dir_group (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_user_group (groupId);
ALTER TABLE dir_user_group ADD CONSTRAINT FK2F0367FDCE539211 FOREIGN KEY (userId) REFERENCES dir_user (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_user_group (userId);
ALTER TABLE dir_user_role ADD CONSTRAINT FK5C5FE738C8FE3CA7 FOREIGN KEY (roleId) REFERENCES dir_role (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_user_role (roleId);
ALTER TABLE dir_user_role ADD CONSTRAINT FK5C5FE738CE539211 FOREIGN KEY (userId) REFERENCES dir_user (id) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON dir_user_role (userId);
ALTER TABLE wf_report ADD CONSTRAINT FKB943CCA47A4E8F48 FOREIGN KEY (packageId) REFERENCES wf_report_package (packageId) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON wf_report (packageId);
ALTER TABLE wf_report ADD CONSTRAINT FKB943CCA4A39D6461 FOREIGN KEY (processDefId) REFERENCES wf_report_process (processDefId) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON wf_report (processDefId);
ALTER TABLE wf_report ADD CONSTRAINT FKB943CCA4CB863F FOREIGN KEY (activityDefId) REFERENCES wf_report_activity (activityDefId) DEFERRABLE INITIALLY DEFERRED;
CREATE INDEX ON wf_report (activityDefId);

-- Sequences --

-- Full Text keys --

COMMIT;
