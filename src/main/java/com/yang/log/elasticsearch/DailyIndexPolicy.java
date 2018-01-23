package com.yang.log.elasticsearch;

import org.apache.http.client.utils.DateUtils;

import java.util.Date;

/**
 * Usage: <b> new index for each day, in the format of [indexPrefix-dateFormat] </b>
 *
 * @author Jingyi.Yang
 *         Date 2018/1/23
 **/
public class DailyIndexPolicy implements LogRollPolicy {
    private final String indexPrefix;
    private final String indexFormat;
    private final String type;

    public DailyIndexPolicy(String indexPrefix, String indexFormat, String type) {
        this.indexPrefix = indexPrefix;
        this.indexFormat = indexFormat;
        this.type = type;
    }

    @Override
    public String getIndexName() {
        return indexPrefix + DateUtils.formatDate(new Date(), indexFormat);
    }

    @Override
    public String getType() {
        return type;
    }
}
