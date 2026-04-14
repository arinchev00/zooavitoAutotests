package helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SiteURL {
    SITE_URL_TUNNEL("https://zooavito.cloudpub.ru/"),
    HOST_TUNNEL_API("http://localhost:8081/v1/api/");
    private final String url;
}
