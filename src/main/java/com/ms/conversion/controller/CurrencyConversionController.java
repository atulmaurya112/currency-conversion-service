package com.ms.conversion.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ms.conversion.model.CurrencyConversion;
import com.ms.conversion.proxy.CurrencyExchangeProxy;

@RestController
public class CurrencyConversionController {
	
	@Autowired
	private CurrencyExchangeProxy exchangeProxy;

	@GetMapping("currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion getConvertedValue(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		HashMap<String, String> pathVariable = new HashMap<>();
		pathVariable.put("from", from);
		pathVariable.put("to", to);
		
		RestTemplate rt = new RestTemplate();
		ResponseEntity<CurrencyConversion> response = rt.getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class,  pathVariable);
		CurrencyConversion cc = response.getBody();
		
		return new CurrencyConversion(1L, from, to, cc.getConversionMultiple(), quantity, quantity.multiply(cc.getConversionMultiple()), cc.getEnvironment() + " rest template");
	}
	
	@GetMapping("currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion getConvertedValueFeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity) {
		CurrencyConversion response = exchangeProxy.getExchangedValue(from, to);
		return new CurrencyConversion(1L, from, to, response.getConversionMultiple(), quantity, quantity.multiply(response.getConversionMultiple()), response.getEnvironment() + " feign");
	}
	
}
