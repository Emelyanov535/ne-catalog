package ru.necatalog.wildberriesparser.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;


@Getter
@Setter
@ConfigurationProperties(prefix = "wildberries")
public class WildberriesConfigProperties {
	private String catalogWbUrl;
	private String laptopUrl;
	private String shard;
	private int appType;
	private int dest;
	private int subject;
	private boolean abTesting;
	private String curr;
	private int hideDtype;
	private String lang;
	private int spp;

	public String buildLaptopCategoryUrl(int page) {
		return UriComponentsBuilder
				.fromUriString(catalogWbUrl)
				.pathSegment(shard)
				.pathSegment(laptopUrl)
				.queryParam("appType", appType)
				.queryParam("dest", dest)
				.queryParam("subject", subject)
				.queryParam("page", page)
				.queryParam("ab_testing", abTesting)
				.queryParam("curr", curr)
				.queryParam("hide_dtype", hideDtype)
				.queryParam("lang", lang)
				.build()
				.toUriString();
	}
}

