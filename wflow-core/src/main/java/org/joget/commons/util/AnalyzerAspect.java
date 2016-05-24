package org.joget.commons.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.joget.apps.datalist.model.DataListBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.service.FormUtil;
import org.joget.apps.userview.model.UserviewMenu;

@Aspect
public class AnalyzerAspect {
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_USERVIEW_PAGE = "userview";
    public static final String TYPE_USERVIEW_MENU = "menu";
    public static final String TYPE_PLUGIN = "plugin";
    public static final String TYPE_FORM_ELEMENT = "formElement";
    public static final String TYPE_FORM_BINDER = "formBinder";
    public static final String TYPE_DATALIST_BINDER = "dataListBinder";
    public static final String TYPE_SQL = "sql";
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static  /* synthetic */ AnalyzerAspect ajc$perSingletonInstance;

    @Pointcut(value="execution(* org.joget.commons.util.AnalyzerFilter.doFilter(..))")
    public /* synthetic */ void requestMethods() {
    }

    @Pointcut(value="execution(* org.joget.plugin.base.Plugin+.*(..)) && !execution(* org.joget.plugin.base.ExtDefaultPlugin+.getPropertyString(..)) && !execution(* org.joget.apps.form.model.Element+.getCustomParameterName(..))")
    private /* synthetic */ void pluginMethods() {
    }

    @Pointcut(value="execution(* java.sql.Statement+.execute*(..)) && !execution(* org.apache.commons.dbcp.DelegatingPreparedStatement.execute*(..))")
    public /* synthetic */ void sqlMethods() {
    }

    @Around(value="requestMethods()")
    public Object analyzeRequests(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = (HttpServletRequest)pjp.getArgs()[0];
        String url = String.valueOf(request.getMethod()) + " " + request.getRequestURI();
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            url = String.valueOf(url) + "?" + query;
        }
        Analyzer.enable();
        Analyzer.activate();
        Analyzer.start("request", "request", url);
        @SuppressWarnings("unused")
		Object ret = null;
        try {
            Object object = ret = pjp.proceed();
            return object;
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
        catch (Throwable t) {
            throw t;
        }
        finally {
            Analyzer.stop(url);
            Analyzer.deactivate();
            Analyzer.disable();
        }
    }

    @Around(value="pluginMethods()")
    public Object analyzePlugins(ProceedingJoinPoint pjp) throws Throwable {
        Object target = pjp.getTarget();
        String className = target.getClass().getSimpleName();
        String method = pjp.getSignature().getName();
        String signature = String.valueOf(className) + "." + method;
        String type = "plugin";
        String id = "";
        String info = "";
        if (target instanceof UserviewMenu) {
            UserviewMenu menu = (UserviewMenu)target;
            if (method.equals("getReadyJspPage") || method.equals("getReadyRenderPage")) {
                type = "userview";
            } else if (method.equals("getMenu")) {
                type = "menu";
            }
            id = menu.getPropertyString("id");
        } else if (target instanceof Element) {
            Element formElement = (Element)target;
            type = "formElement";
            id = FormUtil.getElementParameterName((Element)formElement);
        } else if (target instanceof DataListBinder) {
            type = "dataListBinder";
        }
        Analyzer.start(type, signature, id);
        @SuppressWarnings("unused")
		Object ret = null;
        try {
            Object object = ret = pjp.proceed();
            return object;
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
        catch (Throwable t) {
            throw t;
        }
        finally {
            Analyzer.stop(info);
        }
    }

    @Around(value="sqlMethods()")
    public Object analyzeSql(ProceedingJoinPoint pjp) throws Throwable {
        boolean relevant;
        Object target = pjp.getTarget();
        String method = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        String info = "";
        @SuppressWarnings("unused")
		boolean bl = relevant = method.equals("execute") || method.equals("executeQuery") || method.equals("executeUpdate");
        if (relevant) {
            if (target instanceof PreparedStatement) {
                info = target.toString();
            } else if (target instanceof Statement) {
                info = args != null ? args[0].toString() : "";
            }
            Analyzer.start("sql", String.valueOf(target.getClass().getName()) + "." + method, "");
        }
        @SuppressWarnings("unused")
		Object ret = null;
        try {
            Object object = ret = pjp.proceed();
            return object;
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
        catch (Throwable t) {
            throw t;
        }
        finally {
            if (relevant) {
                Analyzer.stop(info);
            }
        }
    }

    public static AnalyzerAspect aspectOf() {
        if (ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org.joget.commons.util.AnalyzerAspect", ajc$initFailureCause);
        }
        return ajc$perSingletonInstance;
    }

    public static boolean hasAspect() {
        if (ajc$perSingletonInstance != null) {
            return true;
        }
        return false;
    }

    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AnalyzerAspect();
    }

    static {
        try {
            AnalyzerAspect.ajc$postClinit();
        }
        catch (Throwable var0) {
            ajc$initFailureCause = var0;
        }
    }
}
