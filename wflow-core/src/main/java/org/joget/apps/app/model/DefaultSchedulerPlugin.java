package org.joget.apps.app.model;

import org.joget.plugin.base.ExtDefaultPlugin;
import org.quartz.JobExecutionContext;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aristo
 *
 * Default Scheduler Plugin impletemntation and helper
 */
public abstract class DefaultSchedulerPlugin extends ExtDefaultPlugin implements SchedulerPlugin {
    /**
     * NOT USED !!!! For future use !!!
     *
     * @param properties
     * @return NaN
     */
    @Override
    public final Object execute(Map properties) {
        jobRun(properties != null ? properties : new HashMap<>());
        return null;
    }

    /**
     * New Year
     *
     * @param context {@link JobExecutionContext}
     * @return true every new year at 00:00
     */
    public static boolean filterForNewYear(@Nonnull JobExecutionContext context) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(context.getFireTime());
        return calendar.get(Calendar.DAY_OF_YEAR) == 1
                && calendar.get(Calendar.HOUR_OF_DAY) == 0
                && calendar.get(Calendar.MINUTE) == 0;
    }

    /**
     * First Day of Month
     *
     * @param context {@link JobExecutionContext}
     * @return true every first day of month at 00:00
     */
    public static boolean filterForFirstDayOfMonth(@Nonnull JobExecutionContext context) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(context.getFireTime());
        return calendar.get(Calendar.DAY_OF_MONTH) == 1
                && calendar.get(Calendar.HOUR_OF_DAY) == 0
                && calendar.get(Calendar.MINUTE) == 0;
    }

    /**
     * Weekly on specific date
     *
     * @param context {@link JobExecutionContext}
     * @param dayOfWeek
     *      {@link Calendar#SUNDAY}
     *      {@link Calendar#MONDAY}
     *      {@link Calendar#TUESDAY}
     *      {@link Calendar#WEDNESDAY}
     *      {@link Calendar#THURSDAY}
     *      {@link Calendar#FRIDAY}
     *      {@link Calendar#SATURDAY}
     * @return true for every day at 00:00
     */
    public static boolean filterForWeeklyOn(@Nonnull JobExecutionContext context, int dayOfWeek) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(context.getFireTime());
        return calendar.get(Calendar.DAY_OF_WEEK) == dayOfWeek
                && calendar.get(Calendar.HOUR_OF_DAY) == 0
                && calendar.get(Calendar.MINUTE) == 0;
    }

    /**
     * Every Day at 00:00
     *
     * @param context {@link JobExecutionContext}
     * @return true for everyday at 00:00
     */
    public static boolean filterForEveryDay(@Nonnull JobExecutionContext context) {
        return filterForEveryDay(context, 0);
    }

    /**
     * Every Day at specified hour
     *
     * @param context {@link JobExecutionContext}
     * @param atHour 24-hour format
     * @return true for everyday at specified hour
     */
    public static boolean filterForEveryDay(@Nonnull JobExecutionContext context, int atHour) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(context.getFireTime());
        return calendar.get(Calendar.HOUR_OF_DAY) == atHour
                && calendar.get(Calendar.MINUTE) == 0;
    }

    /**
     * Every Hour
     *
     * @param context {@link JobExecutionContext}
     * @return true for every hour
     */
    public static boolean filterForEveryHour(@Nonnull JobExecutionContext context) {
        return filterForEveryIntervalHour(context, 0);
    }

    /**
     * Every Interval Hour
     *
     * @param context {@link JobExecutionContext}
     * @param interval hourly interval
     * @return true for every interval hour starts at 00:00
     */
    public static boolean filterForEveryIntervalHour(@Nonnull JobExecutionContext context, int interval) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(context.getFireTime());
        return calendar.get(Calendar.HOUR_OF_DAY) % (interval < 1 ? 1 : interval) == 0
                && calendar.get(Calendar.MINUTE) == 0;
    }
}
