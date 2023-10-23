package com.kbank.eai.batch.chunk.processor;

import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

import com.kbank.eai.domain.Product;
import com.kbank.eai.domain.ProductVO;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product>{

	@Override
	public Product process(ProductVO item) throws Exception {

		ModelMapper modelMapper = new ModelMapper();
		Product product = modelMapper.map(item, Product.class);
		System.out.println(product.toString());
		
		return product;
	}
	
}
