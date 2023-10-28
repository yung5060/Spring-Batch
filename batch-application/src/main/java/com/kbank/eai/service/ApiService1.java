package com.kbank.eai.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kbank.eai.domain.ApiInfo;
import com.kbank.eai.domain.ApiResponseVO;

@Service
public class ApiService1 extends AbstractApiService {

	@Override
	protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8081/api/product/1", apiInfo, String.class);
		int statusCodeValue = responseEntity.getStatusCodeValue();
		ApiResponseVO apiResponseVO = ApiResponseVO.builder().status(statusCodeValue).msg(responseEntity.getBody()).build();
		
		return apiResponseVO;
	}
	
}
