package com.cscie599.gfn.ingestor.writer;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * An extension of JdbcBatchItemWriter that doesnot rollback on insert failures due to upsert
 * @param <T>
 *
 *
 */
public class UpsertableJdbcBatchItemWriter<T> extends JdbcBatchItemWriter<T> {

    /* (non-Javadoc)
     * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void write(final List<? extends T> items) throws Exception {

        if (!items.isEmpty()) {

            if (logger.isDebugEnabled()) {
                logger.debug("Executing batch with " + items.size() + " items.");
            }

            int[] updateCounts;

            if (usingNamedParameters) {
                if(items.get(0) instanceof Map && this.itemSqlParameterSourceProvider == null) {
                    updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, items.toArray(new Map[items.size()]));
                } else {
                    SqlParameterSource[] batchArgs = new SqlParameterSource[items.size()];
                    int i = 0;
                    for (T item : items) {
                        batchArgs[i++] = itemSqlParameterSourceProvider.createSqlParameterSource(item);
                    }
                    updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);
                }
            }
            else {
                updateCounts = namedParameterJdbcTemplate.getJdbcOperations().execute(sql, new PreparedStatementCallback<int[]>() {
                    @Override
                    public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                        for (T item : items) {
                            itemPreparedStatementSetter.setValues(item, ps);
                            ps.addBatch();
                        }
                        return ps.executeBatch();
                    }
                });
            }

            if (assertUpdates) {
                for (int i = 0; i < updateCounts.length; i++) {
                    int value = updateCounts[i];
                    if (value == 0) {
                        if(logger.isDebugEnabled()) {
                            logger.debug("Skipping retry as this is expected for upserts");
                        }
                    }
                }
            }
        }
    }
}
