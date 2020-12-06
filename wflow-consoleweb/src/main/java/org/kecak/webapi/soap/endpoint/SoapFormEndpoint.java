package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.service.AppService;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.kecak.webapi.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Endpoint
public class SoapFormEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/form/schemas";
    private final Namespace namespace = Namespace.getNamespace("xfs", NAMESPACE_URI);

    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> appVersionExpression;
    private XPathExpression<Element> formDefIdExpression;
    private XPathExpression<Element> assignmentIdExpression;
    private XPathExpression<Element> fieldsExpression;
    private XPathExpression<Element> workflowVariableExpression;

    @Autowired
    private AppService appService;

    @Autowired
    private WorkflowManager workflowManager;

    @Autowired
    private AppDefinitionDao appDefinitionDao;

    public SoapFormEndpoint() {
        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            appIdExpression = xpathFactory.compile("//xfs:appId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appIdExpression = null;
        }

        try {
            appVersionExpression = xpathFactory.compile("//xfs:appVersion", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appVersionExpression = null;
        }

        try {
            assignmentIdExpression = xpathFactory.compile("//xfs:assignmentId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            assignmentIdExpression = null;
        }

        try {
            formDefIdExpression = xpathFactory.compile("//xfs:formDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            formDefIdExpression = null;
        }

        try {
            fieldsExpression = xpathFactory.compile("//xfs:fields", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            fieldsExpression = null;
        }

        try {
            workflowVariableExpression = xpathFactory.compile("//xfs:variables", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            workflowVariableExpression = null;
        }
    }

    /**
     * Submit Form without process
     *
     * @param formSubmitElement
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FormSubmitRequest")
    public @ResponsePayload Element handleFormSubmitRequest(@RequestPayload Element formSubmitElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + formSubmitElement.getName() + "]");

        Element response = new Element("FormSubmitResponse",namespace);

        try {
            @Nonnull final String appId = evaluateValue(formSubmitElement, appIdExpression, "");

            @Nonnull final long appVersion = Optional.ofNullable(evaluateValue(formSubmitElement, appVersionExpression))
                    .map(Long::parseLong)
                    .orElse(appDefinitionDao.getPublishedVersion(appId));

            @Nonnull final String formDefId = evaluateValue(formSubmitElement, formDefIdExpression, "");

            // get current App
            AppDefinition appDefinition = appDefinitionDao.loadVersion(appId, appVersion);
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid application [" + appId + "] version [" + appVersion + "]");
            }

            // get process form
            Form form = appService.viewDataForm(appId, Long.toString(appVersion), formDefId, null, null, null, null, null, null);

            // construct form data
            final FormData formData = (form == null) ? null : fieldsExpression.evaluate(formSubmitElement).stream()
                    .flatMap(elementFields -> elementFields.getChildren().stream())
                    .flatMap(elementMap -> elementMap.getChildren().stream())
                    .collect(FormData::new, (fd, e) -> {
                        String key = e.getChildText("key", namespace);
                        String value = e.getChildText("value", namespace);

                        if (key == null || value == null)
                            return;

                        org.joget.apps.form.model.Element element = FormUtil.findElement(key, form, new FormData());
                        if (element != null)
                            fd.addRequestParameterValues(FormUtil.getElementParameterName(element), new String[]{value});
                    }, (fd1, fd2) -> fd2.getRequestParams().forEach(fd1::addRequestParameterValues));


            FormData resultFormData = appService.submitForm(appId, Long.toString(appVersion), formDefId, formData, false);
            Optional.ofNullable(resultFormData.getFormErrors()).ifPresent(m -> {
                final Element elementValidationError = new Element("validationError",namespace);
                m.forEach((key, value) -> {
                    Element elementField = new Element(key);
                    elementField.addContent(value);
                    elementValidationError.addContent(elementField);
                });
                response.addContent(elementValidationError);
            });

            Optional.ofNullable(resultFormData.getFormResults()).ifPresent(m -> {
                final Element elementResult = new Element("result",namespace);
                m.forEach((key, value) -> {
                    Element elementField = new Element(key);
                    elementField.addContent(value);
                    elementResult.addContent(elementField);
                });
                response.addContent(elementResult);
            });

            Optional.ofNullable(resultFormData.getPrimaryKeyValue())
                    .map(s -> appService.loadFormData(form, s))
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .findFirst()
                    .ifPresent(row -> {
                        final Element elementData = new Element("data",namespace);
                        row.forEach((key, value) -> {
                            Element elementField = new Element(String.valueOf(key));
                            elementField.addContent(String.valueOf(value));
                            elementData.addContent(elementField);
                        });

                        response.addContent(elementData);
                    });

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, e.getMessage());
            response.addContent(e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FormAssignmentCompleteRequest")
    public void handleFormAssignmentCompleteRequest(@RequestPayload Element formAssingmentCompleteElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + formAssingmentCompleteElement.getName() + "]");

        try {
//            @Nonnull final String appId = appIdExpression.evaluate(formAssingmentCompleteElement).get(0).getValue();
//            @Nonnull final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(formAssingmentCompleteElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(formAssingmentCompleteElement).get(0).getValue());
            @Nonnull final String assignmentId = assignmentIdExpression.evaluate(formAssingmentCompleteElement).get(0).getValue();

            WorkflowAssignment assignment = workflowManager.getAssignment(assignmentId);
            if (assignmentId == null) {
                // check if assignment available
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid assignment [" + assignmentId + "]");
            }

            AppDefinition appDefinition = appService.getAppDefinitionForWorkflowProcess(assignment.getProcessId());
            if (appDefinition == null) {
                // check if app valid
                throw new ApiException(HttpServletResponse.SC_BAD_REQUEST, "Invalid process [" + assignment.getProcessId() + "]");
            }

            // get assignment form
            PackageActivityForm packageActivityForm = appService.viewAssignmentForm(appDefinition, assignment, null, "", "");
            final Form form = packageActivityForm.getForm();

            // construct form data
            final FormData formData = (form == null) ? null : fieldsExpression.evaluate(formAssingmentCompleteElement).stream()
                    .flatMap(elementFields -> elementFields.getChildren().stream())
                    .flatMap(elementMap -> elementMap.getChildren().stream())
                    .collect(FormData::new, (fd, e) -> {
                        String key = e.getChildText("key", namespace);
                        String value = e.getChildText("value", namespace);

                        if (key == null || value == null)
                            return;

                        org.joget.apps.form.model.Element element = FormUtil.findElement(key, form, new FormData());
                        if (element != null)
                            fd.addRequestParameterValues(FormUtil.getElementParameterName(element), new String[]{value});
                    }, (fd1, fd2) -> fd2.getRequestParams().forEach(fd1::addRequestParameterValues));


            @Nonnull final Map<String, String> variables = workflowVariableExpression.evaluate(formAssingmentCompleteElement).stream()
                    .flatMap(elementFields -> elementFields.getChildren().stream())
                    .flatMap(elementMap -> elementMap.getChildren().stream())
                    .collect(HashMap::new, (m, e) -> {
                        String key = e.getChildText("key", namespace);
                        String value = e.getChildText("value", namespace);
                        if (key != null && value != null)
                            m.put(key, value);
                    }, Map::putAll);

            appService.completeAssignmentForm(appDefinition.getAppId(), appDefinition.getVersion().toString(), assignmentId, formData, variables);
        } catch (ApiException e) {
            LogUtil.warn(getClass().getName(), e.getMessage());
        }
    }


    private String evaluateValue(Element element, XPathExpression<Element> expression) {
        return evaluateValue(element, expression, null);
    }

    private String evaluateValue(Element element, XPathExpression<Element> expression, String defaultValue) {
        return Optional.ofNullable(element)
                .map(expression::evaluateFirst)
                .map(Element::getValue)
                .orElse(defaultValue);
    }
}
