<%@ tag language="java" trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@ include file="/webpage/include/taglib.jsp"%>
<%@ attribute name="hideType" type="java.lang.String" required="false" description="显示类型"%><!-- 0:隐藏tip, 1隐藏box,不设置显示全部 -->
<%@ attribute name="content" type="java.lang.String" required="true" description="消息内容"%>
<%@ attribute name="type" type="java.lang.String" description="消息类型：info、success、warning、error、loading"%>
