package org.joget.commons.util;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.util.StopWatch;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Timer {
    StopWatch stopWatch;
    String type;
    String id;
    String signature;
    String info;
    long duration;
    Collection<Timer> children = new ArrayList<Timer>();
    Timer parent;
    int depth;

    public String getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getInfo() {
        return this.info;
    }

    public long getDuration() {
        return this.duration;
    }

    public void reset() {
        this.duration = 0;
        this.signature = null;
        this.id = null;
        this.type = null;
        this.info = null;
    }

    public void addChild(Timer timer) {
        this.children.add(timer);
        timer.parent = this;
        timer.depth = this.depth + 1;
    }

    public void detach() {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
            this.parent = null;
        }
    }

    public Collection<Timer> getChildren() {
        return this.children;
    }

    public int getDepth() {
        return this.depth;
    }

    public void start(String type, String signature, String id) {
        this.type = type;
        this.signature = signature;
        this.id = id;
        this.stopWatch = new StopWatch();
        this.stopWatch.start();
    }

    public void stop(String info) {
        if (this.stopWatch != null) {
            this.stopWatch.stop();
            long time = this.stopWatch.getTotalTimeMillis();
            this.info = info;
            this.duration = time;
        }
    }

    public boolean isRunning() {
        return this.stopWatch.isRunning();
    }

    public String toString() {
        String str = this.type + ";";
        if (this.signature != null) {
            str = str + this.signature + ";";
        }
        if (this.id != null) {
            str = str + this.id + ";";
        }
        if (this.info != null) {
            str = str + this.info + ";";
        }
        if (!this.getChildren().isEmpty()) {
            str = str + this.getChildren().size() + ";";
        }
        if (this.duration > 0) {
            str = str + this.duration + "ms";
        }
        return str;
    }
}