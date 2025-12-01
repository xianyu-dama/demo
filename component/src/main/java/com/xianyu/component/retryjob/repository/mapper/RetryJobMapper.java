package com.xianyu.component.retryjob.repository.mapper;

import com.xianyu.component.retryjob.repository.dto.RetryJobQuery;
import com.xianyu.component.retryjob.repository.entity.RetryJobDo;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jspecify.annotations.NonNull;

@Mapper
public interface RetryJobMapper {

    @Select("""
            <script>
            SELECT id,
                   key,
                   type,
                   content,
                   status,
                   current_retry_times AS currentRetryTimes,
                   max_retry_times AS maxRetryTimes,
                   next_execute_time AS nextExecuteTime,
                   last_error_msg AS lastErrorMsg
              FROM retry_job
             WHERE 1=1
               <if test='q.lastId != null'> AND id &gt; #{q.lastId} </if>
               <if test='q.nextExecuteTimeStart != null'> AND next_execute_time &gt;= #{q.nextExecuteTimeStart} </if>
               <if test='q.nextExecuteTimeEnd != null'> AND next_execute_time &lt;= #{q.nextExecuteTimeEnd} </if>
               <if test='q.statusList != null and q.statusList.size() > 0'>
                 AND status IN
                 <foreach collection='q.statusList' item='st' open='(' close=')' separator=','>
                   #{st}
                 </foreach>
               </if>
               <if test='q.retryJobType != null'> AND type = #{q.retryJobType} </if>
             ORDER BY next_execute_time ASC
               <if test='q.count != null'> LIMIT #{q.count} </if>
            </script>
            """)
    @Results(id = "retryJobResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "key", property = "key"),
            @Result(column = "type", property = "type"),
            @Result(column = "content", property = "content"),
            @Result(column = "status", property = "status", typeHandler = org.apache.ibatis.type.EnumTypeHandler.class),
            @Result(column = "currentRetryTimes", property = "currentRetryTimes"),
            @Result(column = "maxRetryTimes", property = "maxRetryTimes"),
            @Result(column = "nextExecuteTime", property = "nextExecuteTime"),
            @Result(column = "lastErrorMsg", property = "lastErrorMsg")
    })
    List<RetryJobDo> list(@NonNull @Param("q") RetryJobQuery q);

    @Select("""
            <script>
            SELECT id,
                   key,
                   type,
                   content,
                   status,
                   current_retry_times AS currentRetryTimes,
                   max_retry_times AS maxRetryTimes,
                   next_execute_time AS nextExecuteTime,
                   last_error_msg AS lastErrorMsg
              FROM retry_job
             WHERE id IN
               <foreach collection='ids' item='id' open='(' close=')' separator=','>
                 #{id}
               </foreach>
            </script>
            """)
    @ResultMap("retryJobResultMap")
    List<RetryJobDo> listByIds(@NonNull @Param("ids") List<Long> ids);

    @Insert("""
            INSERT INTO retry_job (
                   key, type, content, status,
              current_retry_times, max_retry_times, next_execute_time, last_error_msg
            ) VALUES (
              #{key}, #{type}, #{content}, #{status, typeHandler=org.apache.ibatis.type.EnumTypeHandler},
              #{currentRetryTimes}, #{maxRetryTimes}, #{nextExecuteTime}, #{lastErrorMsg}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RetryJobDo item);

    @Select("""
            SELECT id,
                   key,
                   type,
                   content,
                   status,
                   current_retry_times AS currentRetryTimes,
                   max_retry_times AS maxRetryTimes,
                   next_execute_time AS nextExecuteTime,
                   last_error_msg AS lastErrorMsg
              FROM retry_job
             WHERE key = #{key}
               AND type = #{type}
             LIMIT 1
            """)
    @ResultMap("retryJobResultMap")
    Optional<RetryJobDo> get(@Param("type") String type, @Param("key") String key);

    @Update("""
            UPDATE retry_job SET
              key = #{key},
              type = #{type},
              content = #{content},
              status = #{status, typeHandler=org.apache.ibatis.type.EnumTypeHandler},
              current_retry_times = #{currentRetryTimes},
              max_retry_times = #{maxRetryTimes},
              next_execute_time = #{nextExecuteTime},
              last_error_msg = #{lastErrorMsg}
             WHERE id = #{id}
            """)
    int update(RetryJobDo item);

    @Update("""
            <script>
            UPDATE retry_job
               SET status = 0,
                   current_retry_times = 0,
                   next_execute_time = now()
             WHERE id IN
               <foreach collection='ids' item='id' open='(' close=')' separator=','>
                 #{id}
               </foreach>
            </script>
            """)
    int reset(@Param("ids") List<Long> ids);

    @Delete("""
            DELETE FROM retry_job WHERE id = #{id}
            """)
    int delete(@Param("id") Long id);
}
