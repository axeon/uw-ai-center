package uw.ai.center.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.model.ModelOptionsUtils;
import reactor.core.publisher.Flux;

import java.util.function.Function;

/**
 * A simple logger advisor that logs the request and response.
 */
public class AiChatLoggerAdvisor  implements CallAroundAdvisor, StreamAroundAdvisor {

    public static final Function<AdvisedRequest, String> DEFAULT_REQUEST_TO_STRING = request -> request.toString();

    public static final Function<ChatResponse, String> DEFAULT_RESPONSE_TO_STRING = response -> ModelOptionsUtils
            .toJsonString(response);

    private static final Logger logger = LoggerFactory.getLogger( org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor.class);

    private final Function<AdvisedRequest, String> requestToString;

    private final Function<ChatResponse, String> responseToString;

    private int order;

    public AiChatLoggerAdvisor() {
        this(DEFAULT_REQUEST_TO_STRING, DEFAULT_RESPONSE_TO_STRING, 0);
    }

    public AiChatLoggerAdvisor(int order) {
        this(DEFAULT_REQUEST_TO_STRING, DEFAULT_RESPONSE_TO_STRING, order);
    }

    public AiChatLoggerAdvisor(Function<AdvisedRequest, String> requestToString,
                               Function<ChatResponse, String> responseToString, int order) {
        this.requestToString = requestToString;
        this.responseToString = responseToString;
        this.order = order;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        logger.info("request: {}", this.requestToString.apply(request));
        for (Message message : request.messages()) {
            logger.info("message: {}", message);
        }

        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        logger.info("response: {}", this.responseToString.apply(advisedResponse.response()));
    }

    @Override
    public String toString() {
        return org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor.class.getSimpleName();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {

        advisedRequest = before(advisedRequest);

        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

        observeAfter(advisedResponse);

        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

        advisedRequest = before(advisedRequest);

        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }

}