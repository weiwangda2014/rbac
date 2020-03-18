/**
 * 王玮
 */
package com.wandun.common.persistence.interceptor;

import com.wandun.common.persistence.Page;
import com.wandun.common.persistence.dialect.Dialect;
import com.wandun.common.utils.Reflections;
import com.wandun.common.utils.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL工具类
 *
 * @author poplar.yfyang / 云南万盾科技有限公司
 * @version 2013-8-28
 */
public class SQLHelper {

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     *
     * @param ps              表示预编译的 SQL 语句的对象。
     * @param mappedStatement MappedStatement
     * @param boundSql        SQL
     * @param parameterObject 参数对象
     * @throws SQLException 数据库异常
     */
    @SuppressWarnings("unchecked")
    public static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null :
                    configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    @SuppressWarnings("rawtypes")
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }


    /**
     * 查询总纪录数
     *
     * @param sql             SQL语句
     * @param connection      数据库连接
     * @param mappedStatement mapped
     * @param parameterObject 参数
     * @param boundSql        boundSql
     * @return 总记录数
     * @throws SQLException sql查询错误
     */
    public static int getCount(final String sql, final Connection connection,
                               final MappedStatement mappedStatement, final Object parameterObject,
                               final BoundSql boundSql, Log log) throws SQLException {

        CountSqlParser parser = new CountSqlParser();
        final String countSql = parser.getSmartCountSql(sql);

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("COUNT SQL: " + StringUtils.replaceEach(countSql, new String[]{"\n", "\t"}, new String[]{" ", " "}));
            }

            ps = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), countSql,
                    boundSql.getParameterMappings(), parameterObject);
            //解决MyBatis 分页foreach 参数失效 start
            if (Reflections.getFieldValue(boundSql, "metaParameters") != null) {
                MetaObject mo = (MetaObject) Reflections.getFieldValue(boundSql, "metaParameters");
                Reflections.setFieldValue(countBS, "metaParameters", mo);
            }
            //解决MyBatis 分页foreach 参数失效 end
            SQLHelper.setParameters(ps, mappedStatement, countBS, parameterObject);
            rs = ps.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 根据原Sql语句获取对应的查询总记录数的Sql语句
     *
     * @param sql
     * @return
     */
    private static String getCountSql(String sql) {
        //第一步，转换小写
        sql = sql.toLowerCase().trim();
        //第二步，开始遍历 记录from，order，group的下标
        int fromIndex = -1, orderIndex = -1, groupIndex = -1;
        int count = 0;//记录括号数量
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
            } else if (c == 'f' && fromIndex == -1
                    && count == 0 && isMatch(sql, i, "from")) {//如果是from
                fromIndex = i;
            } else if (c == 'o' && orderIndex == -1
                    && count == 0 && isMatch(sql, i, "order")) {//如果是order
                orderIndex = i;
            } else if (c == 'g' && groupIndex == -1
                    && count == 0 && isMatch(sql, i, "group")) {//如果是group
                groupIndex = i;
            }
        }
        //第三步：去掉order by
        if (orderIndex != -1 && orderIndex > groupIndex) {
            sql = sql.substring(0, orderIndex);
        }
        //第四步：根据有无group by拼接全新的sql
        if (groupIndex == -1) {
            sql = "select count(1) " + sql.substring(fromIndex);
        } else {
            sql = "select count(1) from (select 1 " + sql.substring(fromIndex) + ") tbl";
        }
        return sql;
    }

    //判断从source的index开始是否为compare字符串，且两边为空格
    private static boolean isMatch(String source, int index, String compare) {
        if (source == null || compare == null) return false;
        int size = compare.length();
        if (index <= 0 || !isKG(source.charAt(index - 1))) return false;
        if (index + size >= source.length() || !isKG(source.charAt(index + size))) return false;
        return compare.equals(source.substring(index, index + size));
    }

    //判断是否为空格类型符号
    private static boolean isKG(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == '\f';
    }


    /**
     * 根据数据库方言，生成特定的分页sql
     *
     * @param sql     Mapper中的Sql语句
     * @param page    分页对象
     * @param dialect 方言类型
     * @return 分页SQL
     */
    public static String generatePageSql(String sql, Page<Object> page, Dialect dialect) {
        if (dialect.supportsLimit()) {
            return dialect.getLimitString(sql, page.getFirstResult(), page.getMaxResults());
        } else {
            return sql;
        }
    }

    /**
     * 去除qlString的select子句。
     *
     * @param qlString
     * @return
     */
    @SuppressWarnings("unused")
    private static String removeSelect(String qlString) {
        int beginPos = qlString.toLowerCase().indexOf("from");
        return qlString.substring(beginPos);
    }

    /**
     * 去除hql的orderBy子句。
     *
     * @param qlString
     * @return
     */
    @SuppressWarnings("unused")
    private static String removeOrders(String qlString) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(qlString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

}
