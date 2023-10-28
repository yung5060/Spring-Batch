package com.kbank.eai.batch.chunk.processor;

import org.springframework.batch.item.ItemProcessor;

import com.kbank.eai.domain.ApiRequestVO;
import com.kbank.eai.domain.ProductVO;

public class ApiItemProcessor3 implements ItemProcessor<ProductVO, ApiRequestVO>{

	@Override
	public ApiRequestVO process(ProductVO item) throws Exception {
		return ApiRequestVO.builder()
				.id(item.getId())
				.productVO(item)
				.build();
	}
	
}
