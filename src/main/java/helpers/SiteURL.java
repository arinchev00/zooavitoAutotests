package helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SiteURL {
    SITE_URL_TUNNEL("https://zooavito.cloudpub.ru/");
    private final String url;
}
