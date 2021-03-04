package cn.dustlight.live.configurations;

import cn.dustlight.live.Constants;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(title = "Stream Service",
                description = "直播服务接口",
                version = Constants.API_VERSION
        )
)
@SecurityScheme(name = "oauth",
        scheme = "oauth2",
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                implicit = @OAuthFlow(authorizationUrl = Constants.OAuth.AUTHORIZATION_URI,
                        tokenUrl = Constants.OAuth.TOKEN_URI,
                        scopes = {
                                @OAuthScope(name = "read:user"),
                                @OAuthScope(name = "push:stream"),
                                @OAuthScope(name = "write:stream")},
                        refreshUrl = Constants.OAuth.REFRESH_TOKEN_URI)
        ), type = SecuritySchemeType.OAUTH2)
public class DocumentConfiguration {
}
