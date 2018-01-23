package com.yang.log.elasticsearch;

/**
 * Usage: <b> Policy of Log index rolling, defined by index and type </b>
 * <b>By default, will use {@link DailyIndexPolicy}</b>
 *
 * <pre>
 * Select policy by amount of log:
 *  few: same index, simply timestamp
 *  not much: same index, use new type for each day
 *  medium: new index for each day, in the format of [indexPrefix-dateFormat]
 *  Large: slice index by hours, in the format of [indexPrefix-dateTimeFormat]
 * </pre>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/23
 **/
public interface LogRollPolicy {
    String getIndexName();

    String getType();
}
