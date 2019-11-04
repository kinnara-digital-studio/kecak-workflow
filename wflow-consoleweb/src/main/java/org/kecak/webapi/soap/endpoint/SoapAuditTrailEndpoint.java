package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.AuditTrailDao;
import org.joget.apps.app.model.AuditTrail;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.*;


@Endpoint
public class SoapAuditTrailEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> dateFromExpression;
    private XPathExpression<Element> dateToExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;

    @Autowired
    private AuditTrailDao auditTrailDao;

    public SoapAuditTrailEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            dateFromExpression = xpathFactory.compile("//xps:dateFrom", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            dateFromExpression = null;
        }

        try {
            dateToExpression = xpathFactory.compile("//xps:dateTo", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            dateToExpression = null;
        }
        try {
            sortExpression = xpathFactory.compile("//xps:sort", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            sortExpression = null;
        }
        try {
            descExpression = xpathFactory.compile("//xps:desc", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            descExpression = null;
        }
        try {
            startExpression = xpathFactory.compile("//xps:start", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            startExpression = null;
        }
        try {
            rowsExpression = xpathFactory.compile("//xps:rows", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            rowsExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AuditTrailListRequest")
    public @ResponsePayload Element handleAuditTrailList(@RequestPayload Element auditTrailListElement) {
        final String dateFrom = dateFromExpression.evaluate(auditTrailListElement).get(0).getValue();
        final String dateTo = dateToExpression.evaluate(auditTrailListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(auditTrailListElement).get(0).getValue();
        final String descString = descExpression.evaluate(auditTrailListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(auditTrailListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(auditTrailListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AuditTrailListResponse",namespace);

        List<AuditTrail> auditTrailList;

        if (dateFrom != null && dateFrom.trim().length() > 0 && dateTo != null && dateTo.trim().length() > 0) {
            String[] dateFroms = dateFrom.split("-");
            String[] dateTos = dateTo.split("-");

            Calendar dateFromCal = Calendar.getInstance();
            dateFromCal.set(Integer.parseInt(dateFroms[0]), Integer.parseInt(dateFroms[1]) - 1, Integer.parseInt(dateFroms[2]), 0, 0, 0);

            Calendar dateToCal = Calendar.getInstance();
            dateToCal.set(Integer.parseInt(dateTos[0]), Integer.parseInt(dateTos[1]) - 1, Integer.parseInt(dateTos[2]), 23, 59, 59);

            auditTrailList = auditTrailDao.getAuditTrails("where e.timestamp >= ? and e.timestamp <= ?", new Object[]{dateFromCal.getTime(), dateToCal.getTime()}, sort, desc, start, rows);
        } else {
            auditTrailList = auditTrailDao.getAuditTrails(sort, desc, start, rows);
        }

        Element data = new Element("data",namespace);
        for (AuditTrail auditTrail : auditTrailList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(auditTrail.getId()));
            item.addContent(new Element("username",namespace).setText( auditTrail.getUsername()));
            item.addContent(new Element("clazz",namespace).setText( ResourceBundleUtil.getMessage(auditTrail.getClazz(), auditTrail.getClazz())));
            item.addContent(new Element("method",namespace).setText(ResourceBundleUtil.getMessage(auditTrail.getMethod(), auditTrail.getMethod())));
            item.addContent(new Element("message",namespace).setText(auditTrail.getMessage()));
            item.addContent(new Element("timestamp",namespace).setText(TimeZoneUtil.convertToTimeZone(auditTrail.getTimestamp(), null, AppUtil.getAppDateFormat())));
            item.addContent(new Element("id",namespace).setText(auditTrail.getId()));
            data.addContent(item);
        }
        response.addContent(data);
        if (dateFrom != null && dateFrom.trim().length() > 0 && dateTo != null && dateTo.trim().length() > 0) {
            String[] dateFroms = dateFrom.split("-");
            String[] dateTos = dateTo.split("-");

            Calendar dateFromCal = Calendar.getInstance();
            dateFromCal.set(Integer.parseInt(dateFroms[0]), Integer.parseInt(dateFroms[1]) - 1, Integer.parseInt(dateFroms[2]), 0, 0, 0);

            Calendar dateToCal = Calendar.getInstance();
            dateToCal.set(Integer.parseInt(dateTos[0]), Integer.parseInt(dateTos[1]) - 1, Integer.parseInt(dateTos[2]), 23, 59, 59);

            response.addContent(new Element("total",namespace).setText(auditTrailDao.count("where timestamp >= ? and timestamp <=?", new Object[]{dateFromCal.getTime(), dateToCal.getTime()}).toString()));
        } else {
            response.addContent(new Element("total",namespace).setText(auditTrailDao.count("", null).toString()));
        }

        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

}
