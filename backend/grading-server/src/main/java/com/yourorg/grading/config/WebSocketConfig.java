package com.yourorg.grading.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration  // Marks this class as a Spring configuration class
@EnableWebSocketMessageBroker  // Enables WebSocket message handling using STOMP protocol
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Configures the message broker (used to route messages between server and client)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/user/queue", "/topic"); // Enables simple in-memory broker with these prefixes
        config.setUserDestinationPrefix("/user");           // Prefix used for user-specific destinations
    }

    // Registers STOMP endpoints that clients will use to connect to the WebSocket
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-progress")                              // WebSocket endpoint path
                .setAllowedOriginPatterns("http://localhost:5173")        // Allow cross-origin requests from front-end
                .withSockJS();                                            // Enables SockJS fallback for browsers that don’t support WebSocket
    }
}
