package com.leochung0728.quartz.parser.web.stockData;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class WebParserTest {

	@Autowired
	WebParser webParser;

	@Test
	void testSearch() {
		webParser.setSearchParam(MarketType.上市, null);

		webParser.search(3);
		log.debug(webParser.getPageSource());
		Assertions.assertNotNull(webParser.getPageSource());
	}

}
