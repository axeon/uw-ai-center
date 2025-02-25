package uw.ai.center.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.ai.center.entity.AiSessionMsg;
import uw.common.constant.StateCommon;
import uw.dao.DaoFactory;
import uw.dao.TransactionException;

import java.util.Date;

public class AiSessionService {

    private static final Logger logger = LoggerFactory.getLogger( AiSessionService.class );
    private static final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 根据saasId、userId、sessionId获取session.
     *
     * @param saasId
     * @param userId
     * @param sessionId
     * @return
     */
    public static AiSessionInfo loadSession(Long saasId, Long userId, Integer sessionType, Long sessionId) {
        try {
            return dao.queryForSingleObject( AiSessionInfo.class, new AiSessionInfoQueryParam( saasId ).userId( userId ).sessionType( sessionType ).id( sessionId ) );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
        return null;
    }

    /**
     * 初始化session.
     *
     * @param saasId
     * @param userId
     * @param userType
     * @param userInfo
     * @param sessionName
     * @param systemPrompt
     * @param windowSize
     * @return
     */
    public static AiSessionInfo initSession(long saasId, long userId, int userType, String userInfo, int sessionType, String sessionName, String systemPrompt, int windowSize) {
        long sessionId = dao.getSequenceId( AiSessionInfo.class );
        AiSessionInfo sessionInfo = new AiSessionInfo();
        sessionInfo.setId( sessionId );
        sessionInfo.setSaasId( saasId );
        sessionInfo.setUserId( userId );
        sessionInfo.setUserType( userType );
        sessionInfo.setUserInfo( userInfo );
        sessionInfo.setSessionName( truncateWithEllipsis( sessionName, 200 ) );
        sessionInfo.setSystemPrompt( systemPrompt );
        sessionInfo.setMsgNum( 0 );
        sessionInfo.setWindowSize( windowSize );
        sessionInfo.setRequestTokens( 0 );
        sessionInfo.setResponseTokens( 0 );
        sessionInfo.setCreateDate( new java.util.Date() );
        sessionInfo.setLastUpdate( null );
        sessionInfo.setState( StateCommon.ENABLED.getValue() );
        try {
            dao.save( sessionInfo );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
        return sessionInfo;
    }

    /**
     * 初始化sessionMsg.
     *
     * @param sessionId
     * @param userPrompt
     * @return
     */
    public static AiSessionMsg initSessionMsg(long sessionId, String userPrompt) {
        long msgId = dao.getSequenceId( AiSessionMsg.class );
        AiSessionMsg sessionMsg = new AiSessionMsg();
        sessionMsg.setId( msgId );
        sessionMsg.setSessionId( sessionId );
        sessionMsg.setUserPrompt( userPrompt );
        sessionMsg.setState( StateCommon.ENABLED.getValue() );
        sessionMsg.setRequestDate( new Date() );
        return sessionMsg;
    }

    /**
     * 保存sessionMsg.
     *
     * @param sessionMsg
     * @return
     */
    public static AiSessionMsg saveSessionMsg(AiSessionMsg sessionMsg) {
        try {
            // 更新sessionMsg
            dao.save( sessionMsg );
            // 更新session信息
            String sql = "update ai_session_info set last_update=?, msg_num=msg_num+1,request_tokens=request_tokens+?,response_tokens=response_tokens+? where id=?";
            dao.executeCommand( sql, new Object[]{new java.util.Date(), sessionMsg.getRequestTokens(), sessionMsg.getResponseTokens(), sessionMsg.getSessionId()} );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
        return sessionMsg;
    }

    /**
     * 截断字符串，如果长度大于 truncLen，则截断并添加省略号。
     *
     * @param input
     * @param truncLen
     * @return
     */
    public static String truncateWithEllipsis(String input, int truncLen) {
        if (input == null) {
            return null;
        }
        if (input.length() > truncLen) {
            return StringUtils.substring( input, 0, truncLen - 3 ) + "...";
        } else {
            // No truncation needed
            return input;
        }
    }

}
