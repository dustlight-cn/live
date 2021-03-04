package cn.dustlight.live.configurations;

import cn.dustlight.live.repos.StreamHistoryRepository;
import cn.dustlight.live.services.*;
import cn.dustlight.live.repos.StarRepository;
import cn.dustlight.live.repos.StreamRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServicesConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserService userService(@Autowired WebClient webClient) {
        return new OAuthUserService(webClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamService streamService(@Autowired StreamRoomRepository streamRoomRepository,
                                       @Autowired UserService userService,
                                       @Autowired StreamHistoryRepository historyRepository,
                                       @Autowired StarService starService) {
        return new RepositoryStreamService(streamRoomRepository, userService, historyRepository, starService);
    }

    @Bean
    @ConditionalOnMissingBean
    public StarService starService(@Autowired StarRepository starRepository) {
        return new RepositoryStarService(starRepository);
    }
}
