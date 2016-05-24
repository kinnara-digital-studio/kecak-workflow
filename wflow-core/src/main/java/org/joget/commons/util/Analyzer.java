package org.joget.commons.util;

import java.util.Stack;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.HostManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.SetupManager;
import org.joget.commons.util.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Analyzer {
    public static long DEFAULT_THRESHOLD = 100;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static final ThreadLocal<Analyzer> threadAnalyzer = new ThreadLocal();
    boolean enabled = false;
    boolean active = false;
    long threshold = DEFAULT_THRESHOLD;
    @SuppressWarnings({ "unchecked", "rawtypes" })
	Stack<Timer> timerStack = new Stack();
    Timer timer;

    private static Analyzer get() {
        Analyzer analyzer = threadAnalyzer.get();
        if (analyzer == null) {
            analyzer = new Analyzer();
            threadAnalyzer.set(analyzer);
        }
        return analyzer;
    }

    public static void enable() {
        Analyzer analyzer = Analyzer.get();
        SetupManager setupManager = (SetupManager)AppUtil.getApplicationContext().getBean("setupManager");
        @SuppressWarnings("static-access")
		boolean disablePerformanceAnalyzer = Boolean.parseBoolean(setupManager.getSettingValue("disablePerformanceAnalyzer"));
        if (!disablePerformanceAnalyzer && AppUtil.isQuickEditEnabled()) {
            @SuppressWarnings("static-access")
			String thresholdStr = setupManager.getSettingValue("performanceAnalyzerThreshold");
            long threshold = -1;
            if (thresholdStr != null && !thresholdStr.isEmpty()) {
                try {
                    threshold = Long.parseLong(thresholdStr);
                }
                catch (Exception e) {
                    // empty catch block
                }
                if (threshold > 0) {
                    analyzer.threshold = threshold;
                }
            } else {
                analyzer.threshold = DEFAULT_THRESHOLD;
            }
            analyzer.enabled = true;
        } else {
            analyzer.enabled = false;
        }
    }

    public static void disable() {
        Analyzer analyzer = Analyzer.get();
        analyzer.enabled = false;
    }

    public static boolean isEnabled() {
        Analyzer analyzer = Analyzer.get();
        return analyzer.enabled;
    }

    public static void activate() {
        Analyzer analyzer = Analyzer.get();
        analyzer.active = true;
        analyzer.timer = null;
        analyzer.timerStack.clear();
    }

    public static void activate(long threshold) {
        Analyzer analyzer = Analyzer.get();
        analyzer.threshold = threshold;
        analyzer.active = true;
        analyzer.timer = null;
        analyzer.timerStack.clear();
    }

    public static void deactivate() {
        Analyzer analyzer = Analyzer.get();
        analyzer.active = false;
        analyzer.timer = null;
        analyzer.timerStack.clear();
        threadAnalyzer.remove();
    }

    public static boolean isActive() {
        Analyzer analyzer = Analyzer.get();
        return analyzer.active;
    }

    public static void start(String type, String signature, String id) {
        Analyzer analyzer = Analyzer.get();
        if (Analyzer.isEnabled() && Analyzer.isActive()) {
            Timer timer;
            if (analyzer.timerStack.isEmpty()) {
                timer = new Timer();
                analyzer.timerStack.push(timer);
                analyzer.timer = timer;
            } else {
                timer = analyzer.timerStack.peek();
                if (timer.isRunning()) {
                    Timer newTimer = new Timer();
                    timer.addChild(newTimer);
                    analyzer.timerStack.push(newTimer);
                    timer = newTimer;
                }
            }
            timer.start(type, signature, id);
        }
    }

    public static void stop(String info) {
        Analyzer analyzer = Analyzer.get();
        if (Analyzer.isEnabled() && Analyzer.isActive()) {
            Timer timer = analyzer.timerStack.pop();
            timer.stop(info);
            if (timer.getDuration() >= analyzer.threshold) {
                String indent = "";
                for (int i = 0; i < timer.getDepth(); ++i) {
                    indent = indent + "  ";
                }
                String profile = HostManager.getCurrentProfile();
                LogUtil.debug((String)Analyzer.class.getName(), (String)(profile + ": " + indent + "==> " + (Object)timer));
            } else {
                timer.detach();
            }
        }
    }

    public static String getStatus() {
        String status = "{}";
        try {
            Analyzer analyzer = Analyzer.get();
            Timer timer = analyzer.timer;
            JSONObject root = new JSONObject();
            Analyzer.traverse(root, timer);
            Analyzer.jvmStatus(root);
            status = root.toString(4);
        }
        catch (Exception ex) {
            LogUtil.error((String)Analyzer.class.getName(), (Throwable)ex, (String)ex.getMessage());
        }
        return status;
    }

    static void jvmStatus(JSONObject root) throws JSONException {
        int mb = 1048576;
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (long)mb;
        long free = runtime.freeMemory() / (long)mb;
        long total = runtime.totalMemory() / (long)mb;
        long max = runtime.maxMemory() / (long)mb;
        JSONObject jvm = new JSONObject();
        jvm.put("used", used);
        jvm.put("free", free);
        jvm.put("total", total);
        jvm.put("max", max);
        root.put("jvm", (Object)jvm);
    }

    static void traverse(JSONObject root, Timer timer) throws JSONException {
        if (timer != null) {
            root.put("type", (Object)timer.getType());
            root.put("signature", (Object)timer.getSignature());
            root.put("id", (Object)timer.getId());
            root.put("info", (Object)timer.getInfo());
            root.put("duration", timer.getDuration());
            root.put("depth", (Object)Integer.toString(timer.getDepth()));
            JSONArray children = new JSONArray();
            root.put("children", (Object)children);
            for (Timer each : timer.getChildren()) {
                JSONObject child = new JSONObject();
                Analyzer.traverse(child, each);
                children.put((Object)child);
            }
        }
    }
}
