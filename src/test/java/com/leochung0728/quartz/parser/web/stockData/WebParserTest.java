package com.leochung0728.quartz.parser.web.stockData;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leochung0728.quartz.table.Stock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class WebParserTest {

	@Autowired
	WebParser webParser;

	@Test
	void testSearch() {
		webParser.setSearchParam(MarketType.上櫃, null);
		webParser.search(3);
		log.info(webParser.getPageSource());
		Assertions.assertNotNull(webParser.getPageSource());
	}
	
	@Test
	void testParsePageSource() {
		webParser.setSearchParam(MarketType.上櫃, null);
		webParser.search(3);
		List<Stock> stocks = webParser.parsePageSource();
		log.info(stocks.toString());
	}

}
