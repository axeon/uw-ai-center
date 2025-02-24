package uw.ai.center.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uw.ai.center.dto.AiSessionInfoQueryParam;
import uw.ai.center.entity.AiSessionInfo;
import uw.dao.DaoFactory;
import uw.dao.TransactionException;

public class AiSessionService {

    private static final Logger logger = LoggerFactory.getLogger( AiSessionService.class );
    private static final DaoFactory dao = DaoFactory.getInstance();

    /**
     * 根据saasId、userId、sessionId获取session.
     * @param saasId
     * @param userId
     * @param sessionId
     * @return
     */
    public static AiSessionInfo loadSession(long saasId, long userId, long sessionId) {
        try {
            return dao.queryForSingleObject( AiSessionInfo.class, new AiSessionInfoQueryParam( saasId ).userId( userId ).id( sessionId ) );
        } catch (TransactionException e) {
            logger.error( e.getMessage(), e );
        }
        return null;
    }


}
